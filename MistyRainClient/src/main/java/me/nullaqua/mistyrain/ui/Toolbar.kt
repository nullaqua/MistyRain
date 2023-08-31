package me.nullaqua.mistyrain.ui

import javafx.application.Platform
import javafx.stage.FileChooser
import me.lanzhi.api.awt.BluestarLayout
import me.lanzhi.api.awt.BluestarLayoutData
import me.nullaqua.mistyrain.DiaryBaseInfo
import me.nullaqua.mistyrain.Main
import me.nullaqua.mistyrain.Settings
import java.awt.Dimension
import java.awt.datatransfer.DataFlavor
import java.io.File
import javax.swing.*

object Toolbar : JPanel()
{
    const val width = 100
    const val buttonCount = 10
    private val panel: JPanel
    val create: ToolbarButton
    val close: ToolbarButton
    val save: ToolbarButton
    val import: ToolbarButton
    val export: ToolbarButton
    val settings: ToolbarButton
    val importSettings: ToolbarButton
    val exportSettings: ToolbarButton
    val rename: ToolbarButton
    val delete: ToolbarButton
    val about: ToolbarButton

    val editorButton: List<ToolbarButton>

    init
    {
        panel = JPanel()
        this.layout = BluestarLayout()
        val jScrollPane = JScrollPane(panel)
        jScrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        jScrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        this.add(jScrollPane, BluestarLayoutData(1, 1))
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        create  =           ToolbarButton(ImageIcon(Main::class.java.getResource("/image/create.png" )), "新建日记")
        close   =           ToolbarButton(ImageIcon(Main::class.java.getResource("/image/close.png"  )), "关闭日记")
        save    =           ToolbarButton(ImageIcon(Main::class.java.getResource("/image/save.png"   )), "保存日记")
        delete  =           ToolbarButton(ImageIcon(Main::class.java.getResource("/image/delete.png" )), "删除日记")
        rename  =           ToolbarButton(ImageIcon(Main::class.java.getResource("/image/rename.png" )), "重命名日记")
        import  =           ToolbarButton(ImageIcon(Main::class.java.getResource("/image/import.png" )), "导入日记")
        export  =           ToolbarButton(ImageIcon(Main::class.java.getResource("/image/export.png" )), "导出日记")
        importSettings =    ToolbarButton(ImageIcon(Main::class.java.getResource("/image/imset.png"  )), "导入设置")
        exportSettings =    ToolbarButton(ImageIcon(Main::class.java.getResource("/image/exset.png"  )), "导出设置")
        settings =          ToolbarButton(ImageIcon(Main::class.java.getResource("/image/setting.png")), "设置")
        about =             ToolbarButton(ImageIcon(Main::class.java.getResource("/image/about.png"  )), "关于")
        editorButton = listOf(close, save, delete, rename)
        create.addActionListener {
            val title = getDiaryTitle()
            if (title != null)
            {
                DiaryEditor.setDiary(DiaryBaseInfo.create())
            }
        }
        save.addActionListener {
            DiaryEditor.save()
        }
        delete.addActionListener {
            DiaryEditor.delete()
        }
        settings.addActionListener {
            SettingPane.open()
        }
        close.addActionListener {
            DiaryEditor.setDiary(null)
        }
        importSettings.addActionListener {
            val panel = JPanel()
            panel.add(JLabel("将文件拖拽到此处"))
            panel.transferHandler = object : TransferHandler()
            {
                override fun canImport(support: TransferSupport?): Boolean
                {
                    return support?.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == true
                }

                override fun importData(support: TransferSupport?): Boolean
                {
                    if (support?.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == true)
                    {
                        val files = support.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                        if (files.size != 1)
                        {
                            JOptionPane.showMessageDialog(panel, "只能导入一个文件", "错误", JOptionPane.ERROR_MESSAGE)
                            return false
                        }
                        val file = files[0]
                        //如果文件不存在或者文件大于1GB
                        if (!file.exists() || !file.isFile || !file.canRead() || file.length() > 1024 * 1024 * 1024)
                        {
                            JOptionPane.showMessageDialog(
                                panel,
                                "文件错误或文件过大",
                                "错误",
                                JOptionPane.ERROR_MESSAGE
                            )
                            return false
                        }
                        if (file.extension.lowercase() != "mrsetting")
                        {
                            JOptionPane.showMessageDialog(panel, "文件格式错误", "错误", JOptionPane.ERROR_MESSAGE)
                            return false
                        }
                        val s = file.readText()
                        if (!Settings.checkSetting(s))
                        {
                            JOptionPane.showMessageDialog(panel, "文件格式错误", "错误", JOptionPane.ERROR_MESSAGE)
                            return false
                        }
                        Settings.setSettings(s)
                        JOptionPane.showMessageDialog(panel, "导入成功", "成功", JOptionPane.INFORMATION_MESSAGE)
                        return true
                    }
                    return false
                }
            }
            panel.preferredSize = Dimension(300,200)
            JOptionPane.showMessageDialog(MainFrame, panel, "导入设置", JOptionPane.PLAIN_MESSAGE)
        }
        exportSettings.addActionListener {
            //使用javafx的文件选择器
            MainFrame.isEnabled = false
            Platform.runLater {
                val chooser = FileChooser()
                //设置默认文件名
                chooser.initialFileName = "MistyRain.MRSetting"
                //设置文件类型
                chooser.extensionFilters.add(FileChooser.ExtensionFilter("斜风细雨配置文件", "*.MRSetting"))
                val file = chooser.showSaveDialog(null)
                MainFrame.isEnabled = true
                MainFrame.requestFocus()
                if (file != null)
                {
                    val s = Settings.writeToString()
                    file.writeText(s)
                }
            }
        }
        rename.addActionListener { DiaryEditor.rename() }
        about.addActionListener { AboutPane.open() }
        import.addActionListener { ImportPane.open() }
        export.addActionListener { ExportPane.open() }
        this.preferredSize = Dimension(width, 0)
    }

    //日记标题编辑器
    private fun getDiaryTitle(): String?
    {
        return JOptionPane.showInputDialog("请输入日记标题")
    }

    class ToolbarButton(icon: ImageIcon, name: String) :
        JButton(ImageIcon(icon.image.getScaledInstance(70, 70, 0)))
    {
        init
        {
            this.toolTipText = name
            panel.add(this)
        }
    }
}