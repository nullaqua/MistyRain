package me.nullaqua.mistyrain

data class Result(val success: Boolean, val message: String, val error:Throwable?=null)
{
    companion object
    {
        fun success(message: String): Result
        {
            return Result(true, message)
        }

        fun fail(message: String): Result
        {
            return Result(false, message)
        }

        fun success(): Result
        {
            return success("")
        }

        fun fail(throwable: Throwable): Result
        {
            return Result(false, "出现错误!请稍后重试或向开发者反馈", throwable)
        }

        fun fail(message: String, throwable: Throwable): Result
        {
            return Result(false, "出现错误!请稍后重试或向开发者反馈:\n$message", throwable)
        }
    }
}
