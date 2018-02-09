package com.mobgen.halo.android.halochito

import android.database.Cursor
import com.mobgen.halo.android.content.HaloContentApi
import com.mobgen.halo.android.content.models.HaloContentInstance
import com.mobgen.halo.android.content.models.Paginated
import com.mobgen.halo.android.content.models.SearchQuery
import com.mobgen.halo.android.content.models.SearchQuery.Builder
import com.mobgen.halo.android.content.models.SearchSyntax
import com.mobgen.halo.android.content.selectors.HaloContentSelectorFactory
import com.mobgen.halo.android.sdk.api.Halo

/**
 * Created by j.de.pedro.lopez on 2/9/18.
 */
fun query(init: SearchQuery.Builder.() -> Unit): SearchQuery {
    val builder = SearchQuery.builder()
    builder.init()
    return builder.build()
}

fun Builder.search(init: SearchSyntax.() -> Unit): SearchQuery.Builder {
    val search = this.beginSearch()
    search.init()
    return search.end()
}

fun Builder.metaSearch(init: SearchSyntax.() -> Unit): SearchQuery.Builder {
    val search = this.beginMetaSearch()
    search.init()
    return search.end()
}

fun SearchSyntax.group(init: SearchSyntax.() -> Unit): SearchSyntax {
    val search = this.beginGroup()
    search.init()
    return search.endGroup()
}


//
//
//infix fun HaloContentApi.with(halo: Halo): HaloContentApi {
//    return HaloContentApi.with(halo)
//}
//
//object content {
//    infix fun with(halo: Halo): HaloContentApi {
//        return HaloContentApi.with(halo)
//    }
//}
//
//class SearchWrapper(val contentApi: HaloContentApi) {
//    fun search(mode: Int, query: SearchQuery): HaloContentSelectorFactory<Paginated<HaloContentInstance>, Cursor> {
//        return contentApi.search(mode, query)
//    }
//}
//
//fun halo(mode: Int, query: SearchQuery, actions: SearchWrapper.() -> Unit): HaloContentSelectorFactory<Paginated<HaloContentInstance>, Cursor> {
//    val builder = SearchWrapper(this)
//    builder.actions()
//    return builder.search(mode, query)
//}

//fun search(init: SearchSyntax() -> Unit): SearchQuery {
//    val builder = SearchQuery.builder()
//    builder.init()
//    return builder.search()
//}

operator fun Builder.unaryPlus() = this

infix fun Builder.moduleName(moduleName: String): SearchQuery.Builder {
    return moduleName(moduleName)
}


fun hola() {
//    val halo = Halo.installer(null).install()

    SearchQuery.builder()
            .beginSearch()
            .end()
            .moduleName("")
            .build()

    val searchQuery = query {
        moduleIds("12345678")
        moduleName("Hola")
        search {
            eq("name", "2")
            group {
                eq("name", "2")
            }
        }

        metaSearch {

        }
    }

//    HaloContentApi.with(halo).search(1, searchQuery)

//    content with halo {
//        search(searchQuery)
//    }
}