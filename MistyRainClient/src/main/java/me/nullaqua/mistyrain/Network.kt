package me.nullaqua.mistyrain

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException

object Network
{
    const val serverAddress="bluestarmc.top"
    const val serverPort=6099

    private fun connect(): Socket
    {
        val socket=Socket()
        socket.connect(InetSocketAddress(serverAddress, serverPort), 5000)
        return socket
    }

    fun tryLogin(account: Account)= tryLogin(account.username, account.password)

    private fun tryLogin(username: String, password: String): Result
    {
        try
        {
            //连接服务器,超时时间为5秒
            val socket= connect()
            val inputStream= DataInputStream(socket.getInputStream())
            val outputStream= DataOutputStream(socket.getOutputStream())
            outputStream.writeUTF("login")
            outputStream.writeUTF(username)
            outputStream.writeUTF(password)
            val result=inputStream.readUTF()
            socket.close()
            return Result(result == "success", result)
        }
        catch (e:SocketTimeoutException)
        {
            return Result.fail("连接超时,请检查网络连接")
        }
        catch (e: Throwable)
        {
            return Result.fail(e)
        }
    }

    fun tryRegister(account: Account): Result = tryRegister(account.username, account.password)

    private fun tryRegister(username: String, password: String): Result
    {
        try
        {
            val socket= connect()
            val inputStream= DataInputStream(socket.getInputStream())
            val outputStream= DataOutputStream(socket.getOutputStream())
            outputStream.writeUTF("register")
            outputStream.writeUTF(username)
            outputStream.writeUTF(password)
            val result=inputStream.readUTF()
            socket.close()
            return Result(result == "success", result)
        }
        catch (e: Throwable)
        {
            return Result.fail(e)
        }
    }

    fun tryChangePassword(account: Account, oldPassword: String, newPassword: String): Result =
        tryChangePassword(account.username, oldPassword, newPassword)

    private fun tryChangePassword(username: String, oldPassword: String, newPassword: String): Result
    {
        try
        {
            val socket= connect()
            val inputStream= DataInputStream(socket.getInputStream())
            val outputStream= DataOutputStream(socket.getOutputStream())
            outputStream.writeUTF("changePassword")
            outputStream.writeUTF(username)
            outputStream.writeUTF(oldPassword)
            outputStream.writeUTF(newPassword)
            val result=inputStream.readUTF()
            socket.close()
            return Result(result == "success", result)
        }
        catch (e: Throwable)
        {
            return Result.fail(e)
        }
    }
}