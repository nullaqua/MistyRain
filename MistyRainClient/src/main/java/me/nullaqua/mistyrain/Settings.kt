package me.nullaqua.mistyrain

import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatIntelliJLaf
import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.themes.FlatMacDarkLaf
import com.formdev.flatlaf.themes.FlatMacLightLaf
import me.lanzhi.api.reflect.MethodAccessor
import me.nullaqua.mistyrain.ui.MainFrame
import me.nullaqua.mistyrain.ui.MainPane
import me.nullaqua.mistyrain.ui.SettingPane
import java.awt.Color
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.SwingUtilities

object Settings
{
    var theme = Theme.themes()[1]
        set(value)
        {
            field = value
            value.apply()
            write()
        }
    var autoLogin: Account? = null
        set(value)
        {
            field = value
            write()
        }
    var accounts: Set<Account> = mutableSetOf()
        private set(value)
        {
            field = value
            write()
        }
    var dateFormat = "yyyy-MM-dd"
        set(value)
        {
            if (value.isEmpty()||value.contains("H") || value.contains("m") || value.contains("s")) return
            try
            {
                SimpleDateFormat(value)
            }
            catch (e: Exception)
            {
                return
            }
            field = value
            MainPane.update()
            write()
        }

    var cssFile: String =
        run {
            val file = File("themes")
            if (!file.exists())
            {
                val res = javaClass.getResource("/themes.zip")
                val zip = File("themes.zip")
                zip.writeBytes(res.readBytes())
                unzip(zip, file)
            }
            //返回第一个css文件的绝对路径
            file.listFiles()?.firstOrNull { it.isFile && it.extension == "css" }?.absolutePath ?: ""
        }

    val cssUrl : String?
        get()
        {
            return File("C:\\Users\\Huang\\Desktop\\test.css").toURI().toURL().toString()
            val file = File(cssFile)
            if (!file.isFile) return null
            val res=file.toURI().toURL().toString()
            return res
        }

    private val settingsFile
        get() = File("MistyRain.settings")

    fun dateFormat(): SimpleDateFormat = SimpleDateFormat(dateFormat)

    var timeFormat = "HH:mm:ss"
        set(value)
        {
            if (value.isEmpty()||value.contains("y") || value.contains("M") || value.contains("d")) return
            try
            {
                SimpleDateFormat(value)
            }
            catch (e: Exception)
            {
                return
            }
            field = value
            MainPane.update()
            write()
        }

    fun timeFormat(): SimpleDateFormat = SimpleDateFormat(timeFormat)

    fun dateTimeFormat(): SimpleDateFormat = SimpleDateFormat("$dateFormat $timeFormat")



    fun addAccount(account: Account)
    {
        accounts = (accounts as MutableSet<Account>).apply { add(account) }
        if (autoLogin == account) autoLogin = null
    }

    fun removeAccount(account: String)
    {
        removeAccount(Account(account, ""))
    }

    fun removeAccount(account: Account)
    {
        accounts = (accounts as MutableSet<Account>).apply { remove(account) }
    }

    fun writeToString()=
        """
            theme=$theme
            autoLogin=${autoLogin?.username}
            accounts=${if (accounts.isEmpty()) "null" else accounts.joinToString(",") { "${it.username}:${it.password}" }}
            dateFormat=$dateFormat
            timeFormat=$timeFormat
            """.trimIndent()


    fun write()
    {
        settingsFile.writeText(writeToString())
    }

    fun load(save: Boolean = true,file:File= settingsFile)
    {
        if (!file.exists())
        {
            if(save)
            {
                file.createNewFile()
            }
            else throw Exception("File not found")
            return
        }
        val lines = file.readLines()
        var theme: Theme=Theme.themes()[1]
        var autoLogin: Account?=null
        var accounts: Set<Account> = mutableSetOf()
        var dateFormat = "yyyy-MM-dd"
        var timeFormat = "HH:mm:ss"
        lines.forEach {
            val split = it.split("=")
            when (split[0])
            {
                "theme" -> theme = Theme.fromString(split[1])
                "autoLogin" -> autoLogin = if (split[1] == "null") null else Account(split[1], "")
                "accounts" -> accounts = if (split[1] == "null") mutableSetOf()
                else split[1].split(",").map { Account(it.split(":")[0], it.split(":")[1]) }.toMutableSet()

                "dateFormat" -> dateFormat = split[1]
                "timeFormat" -> timeFormat = split[1]
            }
        }
        if (autoLogin != null) autoLogin = accounts.find { it == autoLogin }!!
        if (autoLogin != null) Main.login(autoLogin!!, remember = true, autoLogin = true)
        if (save)
        {
            this.theme = theme
            this.autoLogin = autoLogin
            this.accounts = accounts
            this.dateFormat = dateFormat
            this.timeFormat = timeFormat
        }
    }

    fun setSettings(s:String)
    {
        settingsFile.writeText(s)
        load()
    }

    fun checkSetting(file: String): Boolean
    {
        return try
        {
            load(false,File(file))
            true
        }
        catch (e: Throwable)
        {
            false
        }
    }
}

class Theme(private val method: MethodAccessor, val name: String, val isDark: Boolean)
{
    constructor(clazz: String, name: String, isDark: Boolean) :
            this(Class.forName(clazz), name, isDark)

    constructor(clazz: Class<*>, name: String, isDark: Boolean) :
            this(MethodAccessor.getMethod(clazz, "setup"), name, isDark)

    override fun toString(): String
    {
        return method.method.declaringClass.name
    }

    fun apply()
    {
        method.invoke(null)
        //更新所有UI
        SwingUtilities.updateComponentTreeUI(MainFrame)
        if (SettingPane.window != null) SwingUtilities.updateComponentTreeUI(SettingPane.window)
    }

    companion object
    {
        init
        {
            com.formdev.flatlaf.FlatLaf.setSystemColorGetter { if (it == "accent") color else null }
        }

        private var color: Color? = null

        fun fromString(string: String): Theme
        {
            return themes().find { it.toString() == string }!!
        }

        fun themes(): List<Theme>
        {
            val list = ArrayList<Theme>()
            list.add(Theme(FlatMacLightLaf::class.java, "Mac Light", false))
            list.add(Theme(FlatMacDarkLaf::class.java, "Mac Dark", true))
            list.add(Theme(FlatLightLaf::class.java, "Light", false))
            list.add(Theme(FlatDarkLaf::class.java, "Dark", true))
            list.add(Theme(FlatIntelliJLaf::class.java, "IntelliJ", false))
            list.add(Theme(FlatDarculaLaf::class.java, "Darcula", true))
            com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.INFOS.forEach {
                list.add(Theme(it.className, it.name, it.isDark))
            }
            return list
        }
    }
}

fun Long.formatToDate()=Settings.dateTimeFormat().format(Date(this))