package me.nullaqua.mistyrain

import jdk.internal.loader.*
import me.nullaqua.mistyrain.ui.MainFrame
import javax.swing.JDialog
import javax.swing.JOptionPane


object Main
{
    var account: Account?=null
        private set

    const val version="0.1.0"

    @JvmStatic
    fun main(args: Array<String>)
    {
        MainFrame.isVisible = true
    }

    fun login(account: Account, remember:Boolean, autoLogin:Boolean)
    {
        val jOptionPane=JOptionPane("正在登录,请稍后...",JOptionPane.INFORMATION_MESSAGE,JOptionPane.NO_OPTION,null, arrayOf<Any>(),null)
        val jDialog=jOptionPane.createDialog(MainFrame, "登录")
        jDialog.defaultCloseOperation=JDialog.DO_NOTHING_ON_CLOSE
        Thread()
        {
            val res=Network.tryLogin(account)
            jDialog.dispose()
            login(account,remember,autoLogin,res)
        }.start()
        jDialog.isVisible=true
    }

    fun register(account: Account, remember:Boolean, autoLogin:Boolean)
    {
        val jOptionPane=JOptionPane("正在注册,请稍后...",JOptionPane.INFORMATION_MESSAGE,JOptionPane.NO_OPTION,null, arrayOf<Any>(),null)
        val jDialog=jOptionPane.createDialog(MainFrame, "注册")
        jDialog.defaultCloseOperation=JDialog.DO_NOTHING_ON_CLOSE
        Thread()
        {
            val res=Network.tryRegister(account);
            jDialog.dispose()
            login(account,remember,autoLogin,res)
        }.start()
        jDialog.isVisible=true
    }

    fun logout()
    {
        account =null
        MainFrame.loadUI()
    }

    fun changePassword(oldPassword:String,newPassword:String)
    {
        val jOptionPane=JOptionPane("正在修改密码,请稍后...",JOptionPane.INFORMATION_MESSAGE,JOptionPane.NO_OPTION,null, arrayOf<Any>(),null)
        val jDialog=jOptionPane.createDialog(MainFrame, "修改密码")
        jDialog.defaultCloseOperation=JDialog.DO_NOTHING_ON_CLOSE
        Thread()
        {
            val res=Network.tryChangePassword(account!!, oldPassword, newPassword);
            jDialog.dispose()
            if (res.success)
            {
                JOptionPane.showMessageDialog(MainFrame, "修改密码成功", "修改密码", JOptionPane.INFORMATION_MESSAGE)
            }
            else
            {
                JOptionPane.showMessageDialog(MainFrame, "修改密码失败\n${res.message}", "修改密码", JOptionPane.ERROR_MESSAGE)
            }
        }.start()
        jDialog.isVisible=true
    }

    private fun login(account: Account, remember:Boolean, autoLogin:Boolean, result: Result)
    {
        if (result.success)
        {
            Main.account = account
            if (remember||autoLogin)
            {
                Settings.addAccount(account)
                if (autoLogin)
                {
                    Settings.autoLogin = account
                }
            }
            else
            {
                Settings.removeAccount(account)
            }
            MainFrame.loadUI()
        }
        else
        {
            MainFrame.showError(result)
        }
    }
}