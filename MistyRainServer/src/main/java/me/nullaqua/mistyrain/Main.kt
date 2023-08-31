package me.nullaqua.mistyrain

import me.nullaqua.mistyrain.Result
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.ServerSocket
import java.net.Socket

val accountFile: File
    get() = File("accounts.txt")

val accounts = object : HashMap<String, String>()
{
    override fun put(key: String, value: String): String?
    {
        try
        {
            return super.put(key, value)
        }
        finally
        {
            saveAccounts()
        }
    }

    override fun remove(key: String): String?
    {
        try
        {
            return super.remove(key)
        }
        finally
        {
            saveAccounts()
        }
    }

    override fun remove(key: String, value: String): Boolean
    {
        try
        {
            return super.remove(key, value)
        }
        finally
        {
            saveAccounts()
        }
    }
}

fun saveAccounts()
{
    accountFile.writeText(accounts.map { "${it.key}=${it.value}" }.joinToString("\n"))
}

fun loadAccounts()
{
    if (!accountFile.exists())
    {
        accountFile.createNewFile()
    }
    accounts.clear()
    accountFile.readLines().forEach()
    {
        val split = it.split("=")
        accounts[split[0]] = split[1]
    }
}

fun main()
{
    loadAccounts()
    val server = ServerSocket(6099)
    while (true)
    {
        try
        {
            val socket = server.accept()
            Thread { listen(socket) }.start()
        }
        catch (e: Throwable)
        {
            e.printStackTrace()
        }
    }
}

fun listen(socket: Socket)
{
    val input = DataInputStream(socket.getInputStream())
    val output = DataOutputStream(socket.getOutputStream())
    when (input.readUTF())
    {
        "login" ->
        {
            val username = input.readUTF()
            val password = input.readUTF()
            if (accounts[username] == password)
            {
                output.writeUTF("success")
            }
            else if (accounts.containsKey(username))
            {
                output.writeUTF("密码错误")
            }
            else
            {
                output.writeUTF("账号不存在")
            }
        }

        "register" ->
        {
            val username = input.readUTF()
            val password = input.readUTF()
            if (accounts.containsKey(username))
            {
                output.writeUTF("账号已存在")
            }
            else
            {
                val result = checkUsername(username)
                if (!result.success)
                {
                    output.writeUTF(result.message)
                    return
                }
                val result2 = checkPassword(password)
                if (!result2.success)
                {
                    output.writeUTF(result2.message)
                    return
                }
                accounts[username] = password
                output.writeUTF("success")
            }
        }

        "changePassword" ->
        {
            val username = input.readUTF()
            val oldPassword = input.readUTF()
            val newPassword = input.readUTF()
            if (accounts[username] == oldPassword)
            {
                val result = checkPassword(newPassword)
                if (!result.success)
                {
                    output.writeUTF(result.message)
                    return
                }
                accounts[username] = newPassword
                output.writeUTF("success")
            }
            else if (accounts.containsKey(username))
            {
                output.writeUTF("密码错误")
            }
            else
            {
                output.writeUTF("账号不存在")
            }
        }
    }
}

//检查密码的字符集是否合法
fun checkPassword(s: String): Result
{
    if (s.length !in 6..16)
    {
        return Result.fail("密码长度必须在6到16位之间")
    }
    //密码只能包含数字,字母,常见符号
    if (!s.matches(Regex("[0-9a-zA-Z~!@#\$%^&*_+\\-]+")))
    {
        return Result.fail("密码只能包含数字,字母及!@#\$%^&*_+-")
    }
    return Result.success()
}

fun checkUsername(s: String): Result
{
    if (s.length !in 2..16)
    {
        return Result.fail("用户名长度必须在2到16位之间")
    }
    //用户名只能包含数字字母下划线和中文
    if (!s.matches(Regex("[0-9a-zA-Z_\\u4e00-\\u9fa5]+")))
    {
        return Result.fail("用户名只能包含数字,字母,下划线和中文")
    }
    return Result.success()
}