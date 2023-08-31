package me.nullaqua.mistyrain

data class Diary(
    val info: DiaryBaseInfo,
    val content: String
)
{
    override fun equals(other: Any?) = other is Diary && info == other.info

    override fun hashCode() = info.hashCode()
}

data class DiaryBaseInfo(
    val time: Long,
    var lastModified: Long,
    var backup: Long,
    var title: String,
    var mood: String,
    var weather: String,
    var tags: String
)
{
    override fun toString() = "${Settings.dateFormat().format(time)} ${Settings.timeFormat().format(time)} - ${title.ifBlank { "无标题" }}"

    override fun equals(other: Any?) = other is DiaryBaseInfo && time == other.time

    override fun hashCode() = time.hashCode()

    companion object
    {
        fun create(): DiaryBaseInfo
        {
            val time = System.currentTimeMillis()
            return DiaryBaseInfo(time = time, lastModified = time, backup = time, title = "", mood = "", weather = "", tags = "")
        }
    }
}