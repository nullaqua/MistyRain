package me.nullaqua.mistyrain.ui

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.StackPane
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import me.nullaqua.mistyrain.*
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.io.File
import java.net.URI
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


object DiaryEditor : JPanel()
{
    private val editor = JTextArea()
    private val options = MutableDataSet()
    private val parser = Parser.builder(options).build()
    private val renderer = HtmlRenderer.builder(options).build()
    private val jfxPanel = JFXPanel()
    private var editorSize = editor.font.size
    private lateinit var webEngine: WebEngine
    private lateinit var webView: WebView
    private var saved = true
    private var diary: DiaryBaseInfo? = null

    private val html0= Main::class.java.getResource("/mdeditor.html").readText()

    var enable: Boolean
        get() = editor.isEnabled
        set(value)
        {
            editor.isEnabled = value
        }

    init
    {
        ` `()
        setDiary0(null)
    }

    fun update()
    {
        renderPreview()
        if (diary == null)
        {
            MainFrame.title = null
            editor.text = ""
            Toolbar.editorButton.forEach { it.isVisible = false }
            enable = false
            return
        }
        enable = true
        Toolbar.editorButton.forEach { it.isVisible = true }
        MainFrame.title = diary.toString()
    }

    private fun setDiary0(diary: DiaryBaseInfo?)
    {
        DiaryEditor.diary = diary
        if (diary != null)
        {
            val content = Data.read(diary).content
            editor.text = content
        }
        saved = true
        update()
    }

    fun setDiary(diary: DiaryBaseInfo?)
    {
        if (DiaryEditor.diary == diary) return
        if (DiaryEditor.diary != null && saved.not())
        {
            //弹出对话框,询问是否保存,有三个选项,保存,不保存,取消
            when (JOptionPane.showConfirmDialog(null, "是否保存当前日记?", "提示", JOptionPane.YES_NO_CANCEL_OPTION))
            {
                JOptionPane.YES_OPTION -> save()
                JOptionPane.NO_OPTION ->
                {
                }

                JOptionPane.CANCEL_OPTION -> return
                JOptionPane.CLOSED_OPTION -> return
            }
        }
        setDiary0(diary)
    }

    fun save()
    {
        if (diary == null)
        {
            return
        }
        val content = editor.text
        Data.save(Diary(diary!!, content))
        saved = true
    }

    fun delete()
    {
        if (diary == null)
        {
            return
        }
        if (JOptionPane.showConfirmDialog(
                null,
                "是否删除当前日记?",
                "提示",
                JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION
        )
        {
            Data.delete(diary!!)
            setDiary0(null)
        }
    }

    fun rename()
    {
        if (diary == null) return
        val newName =
            JOptionPane.showInputDialog(null, "请输入新的日记名", "重命名", JOptionPane.PLAIN_MESSAGE) ?: return
        if (newName.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "日记名不能为空", "错误", JOptionPane.ERROR_MESSAGE)
            return
        }
        diary?.title = newName
    }

    private fun getHTML(body: String) =
        run()
        {
            println(Settings.cssUrl)
            //加入css连接
            return@run """
                <!DOCTYPE html>
                <html>
                <body>
                $body
                </body>
                </html>
            """.trimIndent()
        }

    private fun renderPreview()
    {
        val markdown = editor.text
        val html = html0.replace(
            "<textarea id=\"append-test\" style=\"display:none;\"></textarea>",
            "<textarea id=\"append-test\" style=\"display:none;\">$markdown</textarea>"
        )
        InfoBar.show(diary, markdown.length)
        Platform.runLater {
            webEngine.loadContent(html)
        }
    }

    fun ` `()
    {
        editor.document.addDocumentListener(object : DocumentListener
                                            {
                                                override fun insertUpdate(e: DocumentEvent)
                                                {
                                                    renderPreview()
                                                    saved = false
                                                }

                                                override fun removeUpdate(e: DocumentEvent)
                                                {
                                                    renderPreview()
                                                    saved = false
                                                }

                                                override fun changedUpdate(e: DocumentEvent)
                                                {
                                                    renderPreview()
                                                    saved = false
                                                }
                                            })
        val editorScrollPane = JScrollPane(editor)
        editorScrollPane.isWheelScrollingEnabled = false


        Thread {
            while (true)
            {
                if (editor.font.size == editorSize)
                {
                    Thread.sleep(1)
                    continue
                }
                val max = editorScrollPane.verticalScrollBar.maximum
                val value = editorScrollPane.verticalScrollBar.value
                editor.font = editor.font.deriveFont(editorSize.toFloat())
                while (max == editorScrollPane.verticalScrollBar.maximum) doSomething()
                editorScrollPane.verticalScrollBar.value =
                    (1.0 * editorScrollPane.verticalScrollBar.maximum * value / max).toInt()
            }
        }.start()

        editorScrollPane.addMouseWheelListener {
            if (it.isControlDown)
            {
                if (editorSize + it.wheelRotation <= 1)
                {
                    return@addMouseWheelListener
                }
                editorSize += it.wheelRotation
            }
            else if (it.isShiftDown)
            {
                val value = editorScrollPane.horizontalScrollBar.value
                editorScrollPane.horizontalScrollBar.value = value + it.wheelRotation * 20
            }
            else
            {
                val value = editorScrollPane.verticalScrollBar.value
                editorScrollPane.verticalScrollBar.value = value + it.wheelRotation * 20
            }
            it.consume()
        }
        val root = StackPane()
        val scene = Scene(root)
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorScrollPane, jfxPanel)
        splitPane.resizeWeight = 0.5
        splitPane.preferredSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
        this.layout = BorderLayout()
        this.add(InfoBar, BorderLayout.NORTH)
        this.add(splitPane, BorderLayout.CENTER)
        Platform.runLater()
        {
            webView = WebView()
            webEngine = webView.engine
            webEngine.isJavaScriptEnabled = true
            webEngine.userStyleSheetLocation = Settings.cssUrl
            webEngine.load(File(".\\mdeditor\\examples\\out.html").toURI().toURL().toString())
            root.children.add(webView)
            jfxPanel.scene = scene

            webView.childrenUnmodifiable.addListener(ListChangeListener {
                val scrollBarButton = webView.lookupAll(".increment-button")
                for (button in scrollBarButton) button.isVisible = false
            })
            //监听鼠标滚轮事件
            //禁用其自带的鼠标滚轮
            val x = webView.eventDispatcher
            webView.setEventDispatcher()
            { event, tail ->
                if (event is ScrollEvent)
                {
                    if (event.isControlDown)
                    {
                        val zoom = webView.zoom
                        if (event.deltaY > 0) webView.zoom = zoom - 0.1
                        else webView.zoom = zoom + 0.1
                    }
                    else if (event.isShiftDown)
                    {
                        //水平滚动
                        val value = webEngine.executeScript("document.body.scrollLeft") as Int
                        webEngine.executeScript("document.body.scrollLeft=${value + event.deltaY}")
                    }
                    else
                    {
                        //垂直滚动
                        val value = webEngine.executeScript("document.body.scrollTop") as Int
                        webEngine.executeScript("document.body.scrollTop=${value - event.deltaY}")
                    }
                }
                return@setEventDispatcher x.dispatchEvent(event, tail)
            }
            webEngine.locationProperty().addListener()
            { _, _, newValue ->
                if (newValue != null)
                {
                    val uri = URI(newValue)
                    if (uri.scheme == "http" || uri.scheme == "https")
                    {
                        Desktop.getDesktop().browse(uri)
                    }
                    renderPreview()
                }
            }
        }
    }

    fun onClose(): Boolean
    {
        if (saved || diary == null) return true
        val result = JOptionPane.showConfirmDialog(
            MainFrame,
            "有未保存的更改，是否保存？",
            "提示",
            JOptionPane.YES_NO_CANCEL_OPTION
        )
        return when (result)
        {
            JOptionPane.YES_OPTION ->
            {
                save()
                true
            }

            JOptionPane.NO_OPTION -> true
            else -> false
        }
    }
}

object InfoBar : JPanel()
{
    private val id = JLabel()
    private val lastModifie = JLabel()
    private val length = JLabel()
    private val lastBackup = JLabel()

    init
    {
        this.layout = BoxLayout(this, BoxLayout.X_AXIS)
        this.add(id)
        this.add(Box.createHorizontalStrut(10))
        this.add(lastModifie)
        this.add(Box.createHorizontalStrut(10))
        this.add(length)
        this.add(Box.createHorizontalStrut(10))
        this.add(lastBackup)
        this.minimumSize = Dimension(0, 0)
    }

    fun show(diary: DiaryBaseInfo?, len: Int)
    {
        id.isVisible = diary != null
        lastModifie.isVisible = diary != null
        length.isVisible = diary != null
        lastBackup.isVisible = diary != null

        id.text = "ID: ${if (diary != null) "%016x".format(diary.time) else ""}"
        lastModifie.text = "最后修改: ${diary?.lastModified?.formatToDate() ?: ""}"
        length.text = "长度: ${len}字"
        lastBackup.text = "最后备份: ${diary?.backup?.formatToDate() ?: ""}"
    }
}