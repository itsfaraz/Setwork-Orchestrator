package com.designlife.orchestrator.data

public enum class NotificationType {
    TASK_NOTIFY, NOTE_NOTIFY, DECK_NOTIFY, APP_UPDATE, COMMON_NOTIFY
}

object NotificationTypeI{
    fun getType(typeString : String) : NotificationType{
        return if (typeString.equals("TASK_NOTIFY")) NotificationType.TASK_NOTIFY
        else if (typeString.equals("COMMON_NOTIFY")) NotificationType.COMMON_NOTIFY
        else if (typeString.equals("NOTE_NOTIFY")) NotificationType.NOTE_NOTIFY
        else if (typeString.equals("DECK_NOTIFY")) NotificationType.DECK_NOTIFY
        else if (typeString.equals("APP_UPDATE")) NotificationType.APP_UPDATE
        else NotificationType.COMMON_NOTIFY
    }

    fun getTypeString(type : NotificationType) : String{
        return when(type){
            NotificationType.TASK_NOTIFY -> "TASK_NOTIFY"
            NotificationType.NOTE_NOTIFY -> "NOTE_NOTIFY"
            NotificationType.DECK_NOTIFY -> "DECK_NOTIFY"
            NotificationType.APP_UPDATE -> "APP_UPDATE"
            NotificationType.COMMON_NOTIFY -> "COMMON_NOTIFY"
        }
    }
}