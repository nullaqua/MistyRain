package me.nullaqua.mistyrain.ui

import me.lanzhi.api.awt.BluestarLayout
import me.lanzhi.api.awt.BluestarLayoutData
import me.nullaqua.mistyrain.Account
import me.nullaqua.mistyrain.Main
import java.awt.Insets
import javax.swing.*

object RegisterPane: JPanel()
{
    private const val H=4
    init
    {
        val layout = BluestarLayout()
        setLayout(layout)
        var data = BluestarLayoutData(5, H, 0, 0)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        add(JLabel("用户名"), data)
        data = BluestarLayoutData(5, H, 1, 0, 3, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.FILL)
        val usernameField = JTextField()
        add(usernameField, data)
        data = BluestarLayoutData(5, H, 0, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        add(JLabel("密码"), data)
        data = BluestarLayoutData(5, H, 1, 1, 3, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.FILL)
        val passwordField = JPasswordField()
        add(passwordField, data)
        data = BluestarLayoutData(5, H, 4, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.FRONT)
        val showPasswordCheckBox = JCheckBox("显示密码")
        add(showPasswordCheckBox, data)
        data = BluestarLayoutData(5, H, 0, 2)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        add(JLabel("确认密码"), data)
        data = BluestarLayoutData(5, H, 1, 2, 3, 1)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.FILL)
        val confirmPasswordField = JPasswordField()
        add(confirmPasswordField, data)
        data = BluestarLayoutData(5, H, 1, 2, Insets(0, 0, -10, 0))
        data.setPortraitAlignment(BluestarLayoutData.BACK)
        data.setTransverseAlignment(BluestarLayoutData.FRONT)
        val rememberCheckBox = JCheckBox("记住我")
        add(rememberCheckBox, data)
        data = BluestarLayoutData(5, H, 2, 2, Insets(0, 0, -10, 0))
        data.setPortraitAlignment(BluestarLayoutData.BACK)
        data.setTransverseAlignment(BluestarLayoutData.FRONT)
        val autoLoginCheckBox = JCheckBox("自动登录")
        add(autoLoginCheckBox, data)
        data = BluestarLayoutData(5, H, 2, 3)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        val registerButton = JButton("注册")
        add(registerButton, data)
        data = BluestarLayoutData(5, H, 0, 3)
        data.setPortraitAlignment(BluestarLayoutData.CENTER)
        data.setTransverseAlignment(BluestarLayoutData.CENTER)
        val loginButton = JButton("<登录")
        add(loginButton, data)

        autoLoginCheckBox.addActionListener { if (autoLoginCheckBox.isSelected)rememberCheckBox.isSelected = true }
        rememberCheckBox.addActionListener { if (!rememberCheckBox.isSelected)autoLoginCheckBox.isSelected = false }
        loginButton.addActionListener { MainFrame.showLoginPane() }
        showPasswordCheckBox.addActionListener {
            val char=if (showPasswordCheckBox.isSelected)0.toChar() else 8226.toChar()
            passwordField.echoChar=char
            confirmPasswordField.echoChar=char
        }
        registerButton.addActionListener {
            val account = Account(usernameField.text, passwordField.password.joinToString(""))
            Main.register(account, rememberCheckBox.isSelected, autoLoginCheckBox.isSelected)
        }
    }
}