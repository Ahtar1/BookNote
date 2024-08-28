package com.example.booknote.domain.util

sealed class BooksSortOrder {
    data object BookTitleAsc : BooksSortOrder()
    data object BookTitleDesc : BooksSortOrder()
    data object AuthorAsc : BooksSortOrder()
    data object AuthorDesc : BooksSortOrder()
    data object LanguageAsc : BooksSortOrder()
    data object LanguageDesc : BooksSortOrder()
}