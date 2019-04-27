package ru.hse.supertux3.logic.items


class ItemsDictionary {
    companion object {
        val dictionary = mapOf(
            WearableType.GLOVES to listOf(
                Pair("Regular gloves", "Just a regular guys"),
                Pair("Gloves of infinity whatever", "Hell yeah avengers meme")
            ),
            WearableType.HAT to listOf(
                Pair("My hat", "Cool leather hat with Johny Walker badge"),
                Pair("My cap", "Super motherfuckin cool cap with lettering \"Punisher\" on it"),
                Pair("2PAC's bandana", "NIGGA I HIT EM UP!!")
            ),
            WearableType.JACKET to listOf(
                Pair("AU t-shirt", "Fashionable orange t-shirt"),
                Pair("AU hoodie", "Hell yeah avengers meme")
            ),
            WearableType.SHOES to listOf(
                Pair("Ultra golden one choes", "beeeeeeeeeeeee")
            ),
            WearableType.WEAPON to listOf(
                Pair("Bottle of beer", "— В пиве твоя сила и мудрость.\n" +
                        "— Вы любите пиво?\n" +
                        "— Да.\n" +
                        "— А какие сорта предпочитаете?\n" +
                        "— Да обычные. Вот, пожалуйста, толстяк. Нормальное пиво. Пацанское, хули. Пить можно.\n" +
                        "— Похвалите ещё пиво.\n" +
                        "— Ну, как его похвалить, ну, заебись пиво. Пиздатое пиво. Как его ещё похвалить, блядь? Хе-хе.\n" +
                        "— А ещё пара красивых слов?\n" +
                        "— Невъебенное пиво.\n" +
                        "— Спасибо.\n" +
                        "— Да на здоровье. Вот оно, жидкое золото!"),
                Pair("Sword", "Just a stupid sword"),
                Pair("My guitar", "O my god cheesus fuckin christ it s soooo coool"),
                Pair("Gat", "If there's a problem I'ma solve it\n" +
                        "A nigga movin' around with a big-ass revolver")
            ),
            WearableType.PANTS to listOf(
                Pair("pants", "this is pants!")
            )
        )
    }
}