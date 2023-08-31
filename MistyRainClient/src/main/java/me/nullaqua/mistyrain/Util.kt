package me.nullaqua.mistyrain

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

private val `null`=PrintStream(OutputStream.nullOutputStream())

fun doSomething()
{
    `null`.println("Just do something.")
}
fun unzip(file: File, outputDir: File)
{
    var outFile: File
    val zipFile = ZipFile(file)
    val zipInput = ZipInputStream(FileInputStream(file))
    var entry: ZipEntry
    var input: InputStream
    var output: OutputStream
    while (true)
    {
        zipInput.getNextEntry().also { entry = it?:return }
        println("解压缩" + entry.name + "文件")
        outFile = File(outputDir, entry.name)
        if (!outFile.getParentFile().exists()) outFile.getParentFile().mkdir()
        if (!outFile.exists()) outFile.createNewFile()
        input = zipFile.getInputStream(entry)
        output = FileOutputStream(outFile)
        var temp: Int
        while (input.read().also { temp = it } != -1) output.write(temp)
        input.close()
        output.close()
    }
}