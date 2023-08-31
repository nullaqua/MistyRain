package me.nullaqua.mistyrain.ui

import me.lanzhi.api.awt.BluestarLayout
import me.lanzhi.api.awt.BluestarLayoutData
import me.nullaqua.mistyrain.Main
import me.nullaqua.mistyrain.Settings
import me.nullaqua.mistyrain.Theme
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.datatransfer.DataFlavor
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

object SettingPane : JPanel()
{
    private fun readResolve(): Any = SettingPane
    var window: JDialog? = null
        private set

    init
    {
        //最左侧是主题选择列表
        //最右侧是已保存的账号列表,底部2个按钮,一个是切换,一个是删除
        //中心是当前的账号,有退出和修改密码按钮
        //下面一行是css,dateFormat,timeFormat
        layout = BorderLayout()
        add(ThemeList, BorderLayout.WEST)
        add(AccountList, BorderLayout.EAST)
        add(CurrentAccountPane, BorderLayout.CENTER)
        //add(SettingButtonPane,BorderLayout.SOUTH)
    }

    fun update()
    {
        AccountList.update()
        CurrentAccountPane.update()
    }

    fun open()
    {
        update()
        SettingPane.preferredSize = Dimension(800, 600)
        val x = JOptionPane(SettingPane, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf(), null)
        x.createDialog(MainFrame, "轻风细雨 - 设置").apply {
            isResizable = false
            setLocationRelativeTo(null)
            window = this
            isVisible = true
        }
    }
}

object ThemeList : JPanel()
{
    private fun readResolve(): Any = ThemeList
    private val themeList = JPanel()
    private const val width = 200

    init
    {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(JLabel("主题"))
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        //仅黑色主题和仅白色主题勾选框
        val dark = JCheckBox("仅黑色主题")
        val light = JCheckBox("仅白色主题")
        dark.addActionListener { _ ->
            if (dark.isSelected) light.isSelected = false
            update(if (dark.isSelected) 1 else 0)
        }
        light.addActionListener { _ ->
            if (light.isSelected) dark.isSelected = false
            update(if (light.isSelected) -1 else 0)
        }
        panel.add(dark)
        panel.add(light)
        add(panel)
        add(JScrollPane(themeList))
        update(0)
    }

    private fun update(x: Int)
    {
        val themes = Theme.themes()
        themeList.removeAll()
        themeList.layout = BoxLayout(themeList, BoxLayout.Y_AXIS)
        themes.forEach {
            if (x == 1 && it.isDark.not()) return@forEach
            if (x == -1 && it.isDark) return@forEach
            val button = JButton(it.name)
            button.addActionListener { _ ->
                Settings.theme = it
            }
            themeList.add(button)
        }
        preferredSize = Dimension(width, themeList.preferredSize.height)
        updateUI()
    }
}

object AccountList : JPanel()
{
    private fun readResolve(): Any = AccountList
    private val list = JList<String>()
    private const val width = 200

    init
    {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(JLabel("已保存的账号"))
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        add(JScrollPane(list))
        //删除按钮
        val delete = JButton("删除")
        delete.addActionListener { _ ->
            val x = list.selectedValue
            if (x != null)
            {
                Settings.removeAccount(x)
            }
            update()
        }
        add(delete)
    }

    fun update()
    {
        val accounts = Settings.accounts
        list.removeAll()
        list.setListData(accounts.map { it.username }.toTypedArray())
        preferredSize = Dimension(width, list.preferredSize.height)
        updateUI()
    }
}

object CurrentAccountPane : JPanel()
{
    private fun readResolve(): Any = CurrentAccountPane
    private val logout = JButton("退出")
    private val username = JLabel("当前账号: ${Main.account?.username}")
    private val changePassword = JButton("修改密码")
    private val css = JTextArea()
    private val dateFormat = JTextField()
    private val timeFormat = JTextField()

    init
    {
        layout = BluestarLayout()
        var data = BluestarLayoutData(1, 10, 0, 2)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        add(username, data)
        var panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.add(logout)
        panel.add(changePassword)
        data = BluestarLayoutData(1, 10, 0, 3)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        add(panel, data)
        panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.add(JLabel("CSS"))
        val clear = JButton("清空")
        clear.addActionListener { _ ->
            css.text = ""
        }
        panel.add(clear)
        val add = JButton("添加文件")
        add.addActionListener { _ ->
            JOptionPane.showMessageDialog(SettingPane, "将文件拖拽到CSS文本框中即可添加")
        }
        css.transferHandler = object : TransferHandler()
        {
            override fun canImport(support: TransferSupport?): Boolean
            {
                return support?.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == true || support?.isDataFlavorSupported(
                    DataFlavor.stringFlavor
                ) == true
            }

            override fun importData(support: TransferSupport?): Boolean
            {
                if (support?.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == true)
                {
                    val files = support.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                    files.forEach { css.text += it.readText() }
                    return true
                }
                if (support?.isDataFlavorSupported(DataFlavor.stringFlavor) == true)
                {
                    val text = support.transferable.getTransferData(DataFlavor.stringFlavor) as String
                    css.text += text
                    return true
                }
                return false
            }
        }
        panel.add(add)
        data = BluestarLayoutData(1, 10, 0, 5, 1, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.FRONT)
        add(panel, data)
        //css修改时,自动保存
        css.document.addDocumentListener(object : DocumentListener
                                         {
                                             override fun insertUpdate(e: DocumentEvent?)
                                             {
                                                 //Settings.css = css.text
                                             }

                                             override fun removeUpdate(e: DocumentEvent?)
                                             {
                                                 //Settings.css = css.text
                                             }

                                             override fun changedUpdate(e: DocumentEvent?)
                                             {
                                                 //Settings.css = css.text
                                             }
                                         })
        data = BluestarLayoutData(1, 10, 0, 6, 1, 3)
        data.setPortraitAlignment(BluestarLayoutData.FILL)
        data.setTransverseAlignment(BluestarLayoutData.FILL)
        add(JScrollPane(css), data)
        panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.add(JLabel("日期格式"))
        panel.add(dateFormat)
        panel.add(JLabel("时间格式"))
        panel.add(timeFormat)
        val tmp = object : DocumentListener
        {
            override fun insertUpdate(e: DocumentEvent?)
            {
                update0()
            }

            override fun removeUpdate(e: DocumentEvent?)
            {
                update0()
            }

            override fun changedUpdate(e: DocumentEvent?)
            {
                update0()
            }
        }
        dateFormat.document.addDocumentListener(tmp)
        timeFormat.document.addDocumentListener(tmp)
        data = BluestarLayoutData(1, 10, 0, 9)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.FILL)
        add(panel, data)
    }

    fun update()
    {
        val account = Main.account
        username.text = "当前账号: ${account?.username}"
        logout.addActionListener { _ ->
            Main.logout()
        }
        changePassword.addActionListener { _ ->
            //修改密码,一个旧密码,一个新密码,一个确认密码,一个确认,一个取消
            val oldPassword = JPasswordField()
            val newPassword = JPasswordField()
            val confirmPassword = JPasswordField()
            val panel = JPanel()
            panel.layout = BluestarLayout()
            var data = BluestarLayoutData(1, 3)
            data.setPortraitAlignment(BluestarLayoutData.CENTER)
            panel.add(passwordInput("旧密码", oldPassword), data)
            data = BluestarLayoutData(1, 3, 0, 1)
            data.setPortraitAlignment(BluestarLayoutData.CENTER)
            panel.add(passwordInput("新密码", newPassword), data)
            data = BluestarLayoutData(1, 3, 0, 2)
            data.setPortraitAlignment(BluestarLayoutData.CENTER)
            panel.add(passwordInput("确认密码", confirmPassword), data)
            panel.preferredSize = Dimension(300, 100)
            val frame = JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION)
            frame.createDialog(SettingPane, "修改密码").apply {
                isResizable = false
                isVisible = true
                val result = frame.value
                if (result == null || result == JOptionPane.UNINITIALIZED_VALUE || result == JOptionPane.CANCEL_OPTION)
                    return@addActionListener
                if (String(newPassword.password) != String(confirmPassword.password))
                {
                    JOptionPane.showMessageDialog(frame, "两次输入的密码不一致")
                    return@addActionListener
                }
                Main.changePassword(String(oldPassword.password), String(newPassword.password))
            }
        }
        //css.text = Settings.css
        dateFormat.text = Settings.dateFormat
        timeFormat.text = Settings.timeFormat
        updateUI()
    }

    private fun passwordInput(name: String, x: JPasswordField): JPanel
    {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.add(JLabel(name))
        panel.add(x)
        val checkbox = JCheckBox("显示密码")
        checkbox.addActionListener { _ ->
            x.echoChar = if (checkbox.isSelected) 0.toChar() else 8226.toChar()
        }
        panel.add(checkbox)
        return panel
    }

    private fun update0()
    {
        Settings.dateFormat = dateFormat.text
        Settings.timeFormat = timeFormat.text
        if (dateFormat.text != Settings.dateFormat) try
        {
            dateFormat.text = Settings.dateFormat
        }
        catch (_: Exception)
        {
        }
        if (timeFormat.text != Settings.timeFormat) try
        {
            timeFormat.text = Settings.timeFormat
        }
        catch (_: Exception)
        {
        }
    }
}