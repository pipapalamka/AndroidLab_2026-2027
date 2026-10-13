package com.example.hw1.ui.theme


data class Item(
    val id: Int,
    val name: String,
    val description: String,
    val image: String
)

val animals = listOf(
    Item(1, "Пудель", "Пушистый домашний пудель", "https://resizer.mail.ru/p/ace58f67-4cb5-5ebf-86ec-280e4b618edd/AQAORv8dGHWWSgHUvXUNNn_qMiKCAu7JkrQfv-EOkrMxKnPRFJa3RJkfwiVOrtDyzBjwjg-4pbQMiQwbWihzqoeyZ_8.jpg"),
    Item(2, "Пёс", "Верный пес человека", "https://i.pinimg.com/originals/99/b8/1c/99b81c63bf9e9af0e7cf11958542f2b3.jpg"),
    Item(3, "Лиса", "Хитрая лесная лиса", "https://i.pinimg.com/474x/1b/7f/a6/1b7fa6852973330270ca86d748fc3abe.jpg"),
    Item(4, "Медведь", "Хозяин русской тайги", "https://avatars.mds.yandex.net/i?id=a0a412cb6ab6eaf33b0bd3c7e6281373_l-5240239-images-thumbs&n=13"),
    Item(5, "Волк", "Серый хищник - волк", "https://i.pinimg.com/originals/cc/da/f8/ccdaf8905a716f71b76d2735e66a154e.jpg"),
    Item(6, "Заяц", "Быстрый и трусливый зверь", "https://avatars.mds.yandex.net/i?id=d8b179a00ad871dfe31108c012af87a4_l-8287363-images-thumbs&n=13"),
    Item(7, "Ёжик", "Колючий обитатель леса и дворов", "https://i.pinimg.com/originals/f1/f8/18/f1f81889db42f3d5a9920ee58d0f7023.jpg?nii=t"),
    Item(8, "Воробей", "Ночной зверь", "https://avatars.mds.yandex.net/i?id=f1159694e1842d9cae1b88bf861af856_sr-12609997-images-thumbs&n=13"),
    Item(9, "Орёл", "Гордая птица(почти воробей)", "https://avatars.mds.yandex.net/i?id=a3df70d93e5753844b27178b23f399a9_l-4600433-images-thumbs&n=13"),
    Item(10, "Анаконда", "Умное  животное", "https://avatars.mds.yandex.net/i?id=c9a23e4bd2a627bf2138046b8019cfe7_l-4571079-images-thumbs&n=13")
)