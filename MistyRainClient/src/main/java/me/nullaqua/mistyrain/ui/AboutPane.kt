package me.nullaqua.mistyrain.ui

import me.nullaqua.mistyrain.Main
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel

object AboutPane : JPanel()
{
    init
    {
        val info=
            """
            作者: 蓝稚 NullAqua         
            邮箱: nullaqua@outlook.com
            项目名称: 轻风细雨 MistyRain 
            项目地址: https://github.com/nullaqua/MistyRain
            当前版本: ${Main.version}
            项目简介: 一个简单的日记软件
            项目使用的依赖: flexmark,flatlaf,java,java-fx,kotlin
            
            """.trimIndent().replace("\n","<br/>")
        add(JLabel("<html><body>$info</body></html>"))
    }

    fun open()
    {
        JOptionPane.showMessageDialog(MainFrame, AboutPane, "关于", JOptionPane.INFORMATION_MESSAGE)
    }
}