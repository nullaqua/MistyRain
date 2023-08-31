package me.nullaqua.mistyrain.ui

import javafx.application.Platform
import javafx.stage.FileChooser
import me.nullaqua.mistyrain.*
import java.awt.Dimension
import java.awt.datatransfer.DataFlavor
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.util.*
import javax.swing.*
import kotlin.collections.HashMap
import kotlin.math.min

object ImportPane : JPanel()
{
    init
    {
        add(JLabel("将日记拖动到此处来导入"))
        preferredSize= Dimension(300, 200)
        transferHandler = object : TransferHandler()
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
                        JOptionPane.showMessageDialog(MainFrame, "只能导入一个文件", "错误", JOptionPane.ERROR_MESSAGE)
                        return false
                    }
                    val file = files[0]
                    //如果文件不存在或者文件大于1GB
                    if (!file.exists() || !file.isFile || !file.canRead() || file.length() > 100L * 1024 * 1024 * 1024)
                    {
                        JOptionPane.showMessageDialog(MainFrame, "文件错误或文件过大", "错误", JOptionPane.ERROR_MESSAGE)
                        return false
                    }
                    if (file.extension.lowercase(Locale.ENGLISH) != "mrdiary")
                    {
                        JOptionPane.showMessageDialog(MainFrame, "文件格式错误", "错误", JOptionPane.ERROR_MESSAGE)
                        return false
                    }
                    val jOptionPane=JOptionPane("正在导入,请稍后...",JOptionPane.INFORMATION_MESSAGE,JOptionPane.NO_OPTION,null, arrayOf<Any>(),null)
                    val jDialog=jOptionPane.createDialog(MainFrame, "导入")
                    jDialog.defaultCloseOperation=JDialog.DO_NOTHING_ON_CLOSE
                    Thread {
                        try
                        {
                            importFile(file,jDialog::dispose)
                        }
                        catch (e:Throwable)
                        {
                            jDialog.dispose()
                            MainFrame.showError(Result.fail("导入失败", e))
                        }
                    }.start()
                    jDialog.isVisible=true
                }
                return false
            }
        }
    }
    fun open()
    {
        JOptionPane.showMessageDialog(MainFrame, ImportPane, "导入", JOptionPane.PLAIN_MESSAGE)
    }
    private fun importFile(file:File,callBack:()->Unit)
    {
        DataInputStream(file.inputStream()).use {input->
            val count = input.readLong()
            val list = mutableListOf<DiaryBaseInfo>()
            for (i in 1..count) list.add(input.readDiaryInfo())
            val panel = JPanel()
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            val all = JButton("全选")
            val none = JButton("全不选")
            val reverse = JButton("反选")
            val panel1 = JPanel()
            panel1.layout = BoxLayout(panel1, BoxLayout.X_AXIS)
            panel1.add(all)
            panel1.add(Box.createHorizontalStrut(10))
            panel1.add(none)
            panel1.add(Box.createHorizontalStrut(10))
            panel1.add(reverse)
            panel.add(panel1)
            val panel2 = JPanel()
            panel2.layout = BoxLayout(panel2, BoxLayout.Y_AXIS)
            val checkBoxList = mutableListOf<DiaryTitleCheckBox>()
            for (i in list)
            {
                val checkBox = DiaryTitleCheckBox(i)
                checkBoxList.add(checkBox)
                panel2.add(checkBox)
            }
            val scrollPane = JScrollPane(panel2)
            scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
            scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
            panel.add(scrollPane)
            panel.maximumSize=Dimension(400,800)
            panel.preferredSize=Dimension(min(400,panel.preferredSize.width), min(800, panel.preferredSize.height))
            all.addActionListener {
                checkBoxList.forEach { it.isSelected = true }
            }
            none.addActionListener {
                checkBoxList.forEach { it.isSelected = false }
            }
            reverse.addActionListener {
                checkBoxList.forEach { it.isSelected = !it.isSelected }
            }
            callBack()
            val result = JOptionPane.showConfirmDialog(
                MainFrame,
                panel,
                "选择要导入的日记",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            )
            if (result != JOptionPane.OK_OPTION)
            {
                return
            }
            try
            {
                import(checkBoxList, input, count)
                JOptionPane.showMessageDialog(MainFrame, "导入成功", "导入", JOptionPane.INFORMATION_MESSAGE)
            }
            catch (e: Throwable)
            {
                MainFrame.showError(Result.fail("导入失败", e))
            }
        }
    }

    fun import(list:List<DiaryTitleCheckBox>, input:DataInputStream, count:Long)
    {
        val selected=HashMap<Long,DiaryBaseInfo>()
        list.filter { it.isSelected }.forEach { selected[it.diaryInfo.time]=it.diaryInfo }
        for (i in 1..count)
        {
            val time=input.readLong()
            val content=input.readUTF()
            val key=selected.remove(time)
            if (key!=null) Data.save(Diary(key, content))
        }
    }
}
object ExportPane: JPanel()
{
    private val list= mutableListOf<DiaryTitleCheckBox>()
    private val panel2=JPanel()
    private fun update()
    {
        preferredSize=null
        panel2.removeAll()
        list.clear()
        Data.diaryList.forEach { list.add(DiaryTitleCheckBox(it)) }
        for (i in list)
        {
            panel2.add(i)
        }
        maximumSize=Dimension(400,800)
        preferredSize=Dimension(min(400,preferredSize.width),min(800,preferredSize.height))
    }

    init
    {
        this.layout=BoxLayout(this,BoxLayout.Y_AXIS)
        val all=JButton("全选")
        val none=JButton("全不选")
        val reverse=JButton("反选")
        val panel1=JPanel()
        panel1.layout=BoxLayout(panel1,BoxLayout.X_AXIS)
        panel1.add(all)
        panel1.add(Box.createHorizontalStrut(10))
        panel1.add(none)
        panel1.add(Box.createHorizontalStrut(10))
        panel1.add(reverse)
        this.add(panel1)
        panel2.layout=BoxLayout(panel2, BoxLayout.Y_AXIS)
        val scrollPane=JScrollPane(panel2)
        scrollPane.verticalScrollBarPolicy=JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        scrollPane.horizontalScrollBarPolicy=JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        this.add(scrollPane)
        all.addActionListener {
            list.forEach { it.isSelected=true }
        }
        none.addActionListener {
            list.forEach { it.isSelected=false }
        }
        reverse.addActionListener {
            list.forEach { it.isSelected=!it.isSelected }
        }
    }
    fun export(list:List<DiaryBaseInfo>)
    {
        MainFrame.isEnabled = false
        Platform.runLater {
            val chooser = FileChooser()
            //设置默认文件名
            chooser.initialFileName = "MistyRain.MRDiary"
            //设置文件类型
            chooser.extensionFilters.add(FileChooser.ExtensionFilter("斜风细雨日记文件", "*.MRDiary"))
            val file = chooser.showSaveDialog(null)
            MainFrame.isEnabled = true
            MainFrame.requestFocus()
            if (file == null)
            {
                JOptionPane.showMessageDialog(MainFrame, "您取消了导出", "提示", JOptionPane.INFORMATION_MESSAGE)
                return@runLater
            }
            val jOptionPane = JOptionPane("正在导出", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, arrayOf<Any>(), null)
            val jDialog = jOptionPane.createDialog(MainFrame, "导出")
            Thread()
            {
                try
                {
                    export(list, file)
                    JOptionPane.showMessageDialog(MainFrame, "导出成功", "提示", JOptionPane.INFORMATION_MESSAGE)
                }
                catch (e:Exception)
                {
                    jDialog.dispose()
                    JOptionPane.showMessageDialog(MainFrame, "导出失败", "提示", JOptionPane.INFORMATION_MESSAGE)
                }
                finally
                {
                    jDialog.dispose()
                }
            }.start()
            jDialog.isVisible = true
        }
    }

    fun export(list: List<DiaryBaseInfo>, file: File)
    {
        DataOutputStream(file.outputStream()).use {output->
            output.writeLong(list.size.toLong())
            list.forEach { output write it }
            list.forEach {
                output.writeLong(it.time)
                output.writeUTF(Data.read(it).content)
            }
        }
    }

    fun open()
    {
        update()
        val result=JOptionPane.showConfirmDialog(MainFrame, this, "选择要导出的日记", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
        if (result==JOptionPane.OK_OPTION)
        {
            export(list.filter { it.isSelected }.map { it.diaryInfo })
        }
    }
}

class DiaryTitleCheckBox(val diaryInfo: DiaryBaseInfo): JCheckBox(diaryInfo.toString())
{
    init
    {
        isSelected = true
    }
}