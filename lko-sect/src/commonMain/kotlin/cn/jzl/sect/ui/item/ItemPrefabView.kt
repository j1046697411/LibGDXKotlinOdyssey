package cn.jzl.sect.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.sect.MainWorld
import cn.jzl.sect.currentWorld
import cn.jzl.sect.ecs.core.Description
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.item.Amount
import cn.jzl.sect.ecs.item.Stackable
import cn.jzl.sect.ecs.item.UnitPrice
import cn.jzl.sect.ui.UIEntity
import cn.jzl.sect.ui.Updater

@Composable
inline fun <reified T : Any> service(tag: Any? = null): T {
    val world = currentWorld
    return remember(world) {
        val service by world.di.instance<T>(tag)
        service
    }
}

class ItemModel(override val world: World) : Updater, WorldOwner {
    var name: String by mutableStateOf("")
        private set

    var description: String by mutableStateOf("")
        private set

    var stackable: Boolean by mutableStateOf(false)
        private set

    var amount: Int by mutableStateOf(0)
        private set

    var unitPrice: Int by mutableStateOf(0)
        private set

    override val involvingRelations: Sequence<Relation> = sequence {
        yield(relations.component<Named>())
        yield(relations.component<Stackable>())
        yield(relations.component<Amount>())
        yield(relations.component<UnitPrice>())
        yield(relations.component<Description>())
    }

    override fun EntityRelationContext.update(entity: Entity) {
        name = entity.getComponent<Named>().name
        description = entity.getComponent<Description>().description
        unitPrice = entity.getComponent<UnitPrice>().value
        stackable = entity.hasTag<Stackable>()

        amount = entity.getComponent<Amount?>()?.value ?: 1
    }
}

@Composable
fun InventoryItemView(entity: Entity, modifier: Modifier = Modifier) = UIEntity(entity, ::ItemModel) {
    Box(modifier.fillMaxSize(1f).background(Color.Black)) {
        Column(Modifier.align(Alignment.Center)) {
            Text(it.name, color = Color.White)
            Text(it.description, color = Color.White)
            Text("${it.unitPrice} 金币/个", color = Color.White)
            if (it.amount > 1) {
                Text("${it.amount} 个", color = Color.White)
            }
        }
    }
}

@Composable
fun InventoryItemListView(items: List<Entity>) = MainWorld {
    LazyVerticalGrid(GridCells.Fixed(4)) {
        items(items) {
            InventoryItemView(it)
        }
    }
}