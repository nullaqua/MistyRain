package me.nullaqua.mistyrain

import me.nullaqua.api.serialize.Serialize
import me.nullaqua.mistyrain.ui.MainPane
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.util.*

object Data
{
    val diaryList: List<DiaryBaseInfo>
        get()
        {
            val dir = File("data")
            if (!dir.exists())
            {
                dir.mkdirs()
            }
            return (dir.listFiles()?.map()
            {file ->
                if (!file.name.matches("[0-9A-F]{16}\\.MistyRain\\.diary".toRegex()))return@map null
                return@map DataInputStream(file.inputStream()).use {input->
                    val bytes=ByteArray(input.readInt())
                    input.readFully(bytes)
                    Serialize.deserialize(bytes).deserialize(true) as? DiaryBaseInfo
                }
            }?: listOf()).filterNotNull()
        }

    fun save(diary: Diary)
    {
        val dir = File("data")
        if (!dir.exists())
        {
            dir.mkdirs()
        }
        //文件名是时间戳转为16位16进制
        val file = File(dir, "${"%016x".format(diary.info.time).uppercase()}.MistyRain.diary")
        //内容使用java.util.zip压缩
        DataOutputStream(file.outputStream()).use { out ->
            val bytes=Serialize.serialize(diary.info).toBytes()
            out.writeInt(bytes.size)
            out.write(bytes)
            out.writeUTF(diary.content)
        }
        MainPane.update()
    }

    fun read(diaryInfo: DiaryBaseInfo): Diary
    {
        val dir = File("data")
        if (!dir.exists())
        {
            dir.mkdirs()
        }
        val file = File(dir, "${"%016x".format(diaryInfo.time).uppercase()}.MistyRain.diary")
        if (!file.exists())
        {
            save(Diary(diaryInfo, ""))
            return Diary(diaryInfo, "")
        }
        DataInputStream(file.inputStream()).use { input ->
            val bytes=ByteArray(input.readInt())
            input.readFully(bytes)
            val info=Serialize.deserialize(bytes).deserialize(true) as? DiaryBaseInfo?:throw Exception("格式错误")
            return Diary(info, input.readUTF())
        }
    }

    fun delete(diary: Diary)= delete(diary.info)

    fun delete(diaryInfo: DiaryBaseInfo)
    {
        val dir = File("data")
        if (!dir.exists())
        {
            dir.mkdirs()
        }
        val file = File(dir, "${"%016x".format(diaryInfo.time).uppercase()}.MistyRain.diary")
        if (file.exists())
        {
            file.delete()
        }
        MainPane.update()
    }
}

infix fun DataOutputStream.write(info:DiaryBaseInfo)
{
    val bytes=Serialize.serialize(info).toBytes()
    writeInt(bytes.size)
    write(bytes)
}

fun DataInputStream.readDiaryInfo():DiaryBaseInfo
{
    val bytes=ByteArray(readInt())
    readFully(bytes)
    return Serialize.deserialize(bytes).deserialize(true) as? DiaryBaseInfo?:throw Exception("格式错误")
}