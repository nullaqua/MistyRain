package me.nullaqua.mistyrain.ui

import me.nullaqua.mistyrain.Result
import me.nullaqua.mistyrain.Main
import me.nullaqua.mistyrain.Settings
import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.*
import kotlin.system.exitProcess


object MainFrame : JFrame()
{
    init
    {
        Settings.load()
        super.setTitle("轻风细雨")
        setSize(300, 200)
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        Settings.theme.apply()
        this.addWindowListener(object : java.awt.event.WindowAdapter()
        {
            override fun windowClosing(e: java.awt.event.WindowEvent?)
            {
                if (DiaryEditor.onClose()) exitProcess(0)
            }
        })
        loadUI()
    }

    override fun setTitle(title: String?)
    {
        if (title == null) return super.setTitle("轻风细雨")
        super.setTitle("轻风细雨 - $title")
    }

    fun loadUI()
    {
        SettingPane.window?.dispose()
        if (Main.account == null)
        {
            showLoginPane()
        }
        else
        {
            showMainPane()
        }
    }

    fun showMainPane()
    {
        setSize(1000, 800)
        minimumSize = Dimension(MainPane.totalWidth, 600)
        isResizable = true
        contentPane = MainPane
        toCenter()
    }

    fun showLoginPane()
    {
        minimumSize = Dimension(400, 200)
        setSize(400, 200)
        isResizable = false
        LoginPane.update()
        contentPane = LoginPane
        toCenter()
    }

    fun showRegisterPane()
    {
        minimumSize = Dimension(400, 250)
        setSize(400, 250)
        isResizable = false
        contentPane = RegisterPane
        toCenter()
    }

    fun showError(message: String)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
    }

    fun showError(result: Result)
    {
        if (result.success) return
        if (result.error == null) return showError(result.message)
        //在弹窗中先显示错误信息,再显示堆栈信息,其中堆栈信息用一个滚动面板来显示
        val textArea = JTextArea(result.error!!.stackTraceToString())
        textArea.isEditable = false
        val scrollPane = JScrollPane(textArea)
        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        val pane = JPanel()
        pane.layout = BoxLayout(pane, BoxLayout.Y_AXIS)
        //先用红色显示错误信息
        val label = JLabel(result.message)
        label.foreground = Color.RED
        pane.add(JLabel(" "))
        pane.add(label)
        pane.add(JLabel(" "))
        pane.add(scrollPane)
        pane.preferredSize = Dimension(400, 100)
        JOptionPane.showMessageDialog(this, pane, "Error", JOptionPane.ERROR_MESSAGE)
    }

    fun toCenter()
    {
        val width = Toolkit.getDefaultToolkit().screenSize.width
        val height = Toolkit.getDefaultToolkit().screenSize.height
        val windowWidth = size.width
        val windowHeight = size.height
        setLocation((width - windowWidth) / 2, (height - windowHeight) / 2)
    }
}