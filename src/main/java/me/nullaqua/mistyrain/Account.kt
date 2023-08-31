package me.nullaqua.mistyrain

data class Account(val username: String, val password: String)
{
    override fun equals(other: Any?)=other is Account && other.username==username

    override fun hashCode()=username.hashCode()

    override fun toString()=username
}