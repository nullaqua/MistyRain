package me.nullaqua.mistyrain.ui

import me.lanzhi.api.awt.BluestarLayout
import me.lanzhi.api.awt.BluestarLayoutData
import me.nullaqua.mistyrain.Account
import me.nullaqua.mistyrain.Main
import me.nullaqua.mistyrain.Settings
import java.awt.Insets
import javax.swing.*

object LoginPane: JPanel()
{
    private val usernameComboBox: JComboBox<Account> = JComboBox()
    init
    {
        //第一行,一个"用户名",一个用户名输入框,一个已记录的用户名下拉框
        //第二行,一个"密码"
        //第三行,一个"记住我"的复选框,一个"自动登录"的复选框
        //第四行,一个"登录"按钮,一个"去注册"按钮
        val layout = BluestarLayout()
        setLayout(layout)
        var data = BluestarLayoutData(5, 3, 0, 0)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        add(JLabel("用户名"), data)
        data = BluestarLayoutData(5, 3, 1, 0, 3, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.FILL)
        val usernameField = JTextField()
        add(usernameField, data)
        data = BluestarLayoutData(5, 3, 4, 0)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        add(usernameComboBox, data)
        data = BluestarLayoutData(5, 3, 0, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        add(JLabel("密码"), data)
        data = BluestarLayoutData(5, 3, 1, 1, 3, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.FILL)
        val passwordField = JPasswordField()
        add(passwordField, data)
        data = BluestarLayoutData(5, 3, 4, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.FRONT)
        val showPasswordCheckBox = JCheckBox("显示密码")
        add(showPasswordCheckBox, data)
        data = BluestarLayoutData(5, 3, 1, 1, Insets(0,0,-10,0))
        data.setPortraitAlignment(BluestarLayoutData.BACK)
        data.setTransverseAlignment(BluestarLayoutData.FRONT)
        val rememberCheckBox = JCheckBox("记住我")
        add(rememberCheckBox, data)
        data = BluestarLayoutData(5, 3, 2, 1, Insets(0,0,-10,0))
        data.setPortraitAlignment(BluestarLayoutData.BACK)
        data.setTransverseAlignment(BluestarLayoutData.FRONT)
        val autoLoginCheckBox = JCheckBox("自动登录")
        add(autoLoginCheckBox, data)
        data = BluestarLayoutData(5, 3, 2, 2)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        val loginButton = JButton("登录")
        add(loginButton, data)
        data = BluestarLayoutData(5, 3, 4, 2)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        val registerButton = JButton("注册>")
        add(registerButton, data)

        autoLoginCheckBox.addActionListener { if (autoLoginCheckBox.isSelected)rememberCheckBox.isSelected = true }
        rememberCheckBox.addActionListener { if (!rememberCheckBox.isSelected)autoLoginCheckBox.isSelected = false }
        showPasswordCheckBox.addActionListener {
            passwordField.echoChar = if (showPasswordCheckBox.isSelected) 0.toChar() else 8226.toChar()
        }
        registerButton.addActionListener { MainFrame.showRegisterPane() }
        usernameComboBox.addActionListener {
            usernameField.text = (usernameComboBox.selectedItem as? Account)?.username ?: ""
            passwordField.text = (usernameComboBox.selectedItem as? Account)?.password ?: ""
            rememberCheckBox.isSelected = true
            autoLoginCheckBox.isSelected = (usernameComboBox.selectedItem as Account)== Settings.autoLogin
        }
        loginButton.addActionListener {
            val account = Account(usernameField.text, passwordField.password.joinToString(""))
            Main.login(account, rememberCheckBox.isSelected, autoLoginCheckBox.isSelected)
        }
    }

    fun update()
    {
        usernameComboBox.removeAllItems()
        Settings.accounts.forEach { usernameComboBox.addItem(it) }
    }
}