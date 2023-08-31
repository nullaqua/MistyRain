package me.nullaqua.mistyrain.ui

import me.lanzhi.api.awt.BluestarLayout
import me.lanzhi.api.awt.BluestarLayoutData
import me.nullaqua.mistyrain.Data
import me.nullaqua.mistyrain.DiaryBaseInfo
import me.nullaqua.mistyrain.Settings
import java.awt.Insets
import java.util.*
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane

object MainPane : JPanel()
{
    private val diaryList: JPanel
    private const val diaryListWidth = 115
    val totalWidth: Int

    init
    {
        layout = BluestarLayout()
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        add(splitPane, BluestarLayoutData(1, 1, 0, 0, 1, 1, Insets(0, Toolbar.width, 0, 0)))
        diaryList = JPanel()
        val diaryList = JScrollPane(diaryList)
        splitPane.leftComponent = diaryList
        splitPane.rightComponent = DiaryEditor
        val data = BluestarLayoutData(1, 1)
        data.setPortraitAlignment(BluestarLayoutData.FILL)
        data.setTransverseAlignment(BluestarLayoutData.FRONT)
        add(Toolbar, data)
        updateDiaryList()
        //设置diaryList始终显示纵向滚动条,始终不显示横向滚动条
        diaryList.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        diaryList.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        diaryList.minimumSize = diaryList.preferredSize
        totalWidth = diaryList.preferredSize.width + Toolbar.width
    }

    fun update()
    {
        updateDiaryList()
        DiaryEditor.update()
    }

    private fun updateDiaryList()
    {
        diaryList.removeAll()
        diaryList.layout = BoxLayout(diaryList, BoxLayout.Y_AXIS)
        val list = Data.diaryList
        val size = list.size
        for (i in 0 until size)
        {
            val diary = list[size - i - 1]
            val button = DiaryButton(diary)
            diaryList.add(button)
        }
        var x= diaryList.minimumSize
        x.width= diaryListWidth
        diaryList.minimumSize=x
        x= diaryList.preferredSize
        x.width= diaryListWidth
        diaryList.preferredSize=x
        updateUI()
    }

    //在按钮上显示日记标题,创建日期,编辑日期
    class DiaryButton(private val diary: DiaryBaseInfo) : JButton(
        "<html>${diary.title.ifBlank { "无标题" }}<br>" +
                "${Settings.dateFormat().format(Date(diary.time))} ${Settings.timeFormat().format(Date(diary.time))}" +
                "</html>"
    )
    {
        init
        {
            addActionListener {
                DiaryEditor.setDiary(diary)
            }
        }
    }
}