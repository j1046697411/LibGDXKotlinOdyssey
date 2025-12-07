package cn.jzl.sect.ui.market

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.forEach
import cn.jzl.ecs.query.query
import cn.jzl.ecs.query.singleQuery
import cn.jzl.sect.MainWorld
import cn.jzl.sect.currentWorld
import cn.jzl.sect.ecs.Money
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.core.OwnedBy
import cn.jzl.sect.ecs.item.Amount
import cn.jzl.sect.ecs.item.ItemService
import cn.jzl.sect.ecs.market.*
import cn.jzl.sect.ui.UIEntity
import cn.jzl.sect.ui.Updater
import kotlinx.coroutines.delay

class MarketViewModel(
    override val world: World,
    private val market: Entity
) : Updater, WorldOwner {

    val consignmentOrder = mutableStateListOf<Entity>()
    val acquisitionOrder = mutableStateListOf<Entity>()

    var name: String by mutableStateOf("")
        private set

    override val involvingRelations: Sequence<Relation> = sequence {
        yield(relations.component<Named>())
    }

    private val consignmentOrderQuery = world.query {
        object : EntityQueryContext(this) {
            override fun FamilyMatcher.FamilyBuilder.configure() {
                relation(relations.relation<ConsignmentOrder>(market))
            }
        }
    }

    private val acquisitionOrderQuery = world.query {
        object : EntityQueryContext(this) {

            override fun FamilyMatcher.FamilyBuilder.configure() {
                relation(relations.relation<AcquisitionOrder>(market))
            }
        }
    }

    override fun EntityRelationContext.update(entity: Entity) {
        name = entity.getComponent<Named>().name
        acquisitionOrder.clear()
        acquisitionOrderQuery.forEach {
            println("AcquisitionOrderViewModel update ${this.entity}")
            acquisitionOrder.add(this.entity)
        }
        consignmentOrder.clear()
        consignmentOrderQuery.forEach {
            println("ConsignmentOrderViewModel update ${this.entity}")
            consignmentOrder.add(this.entity)
        }
    }
}

class AcquisitionOrderViewModel(override val world: World) : Updater, WorldOwner {

    var owner: Entity? by mutableStateOf(null)
        private set

    var ownerName: String by mutableStateOf("")
        private set

    var totalValue: Int by mutableStateOf(0)
        private set

    var acquisitionItems = mutableStateListOf<AcquisitionItem>()

    override val involvingRelations: Sequence<Relation> = sequence {
        yield(relations.component<OwnedBy>())
        yield(relations.kind<AcquisitionItemData>())
        yield(relations.component<Named>())
    }

    override fun EntityRelationContext.update(entity: Entity) {
        val owner = entity.getRelationUp<OwnedBy>()
        ownerName = owner?.getComponent<Named?>()?.name ?: "No Owner"
        this@AcquisitionOrderViewModel.owner = owner
        totalValue = 0
        acquisitionItems.clear()
        entity.getRelationsWithData<AcquisitionItemData>().forEach {
            totalValue += it.data.count * it.data.unitPrice
            val name = it.relation.target.getComponent<Named>()
            acquisitionItems.add(AcquisitionItem(it.relation.target, it.data, name))
        }
    }

    data class AcquisitionItem(
        val itemPrefab: Entity,
        val data: AcquisitionItemData,
        val itemName: Named
    )
}

class ConsignmentOrderViewModel(override val world: World) : Updater, WorldOwner {

    var owner: Entity? by mutableStateOf(null)
        private set

    var ownerName: String by mutableStateOf("")
        private set

    var totalValue: Int by mutableStateOf(0)
        private set

    var consignmentItems = mutableStateListOf<ConsignmentItem>()

    override val involvingRelations: Sequence<Relation> = sequence {
        yield(relations.component<OwnedBy>())
        yield(relations.kind<ConsignmentItemData>())
        yield(relations.component<Named>())
    }

    override fun EntityRelationContext.update(entity: Entity) {
        val owner = entity.getRelationUp<OwnedBy>()
        ownerName = owner?.getComponent<Named?>()?.name ?: "No Owner"
        this@ConsignmentOrderViewModel.owner = owner
        totalValue = 0
        consignmentItems.clear()
        world.singleQuery { ConsignmentItemEntity(this, entity) }.forEach {
            val amount = amount?.value ?: 1
            totalValue += consignmentItemData.unitPrice * amount
            consignmentItems.add(ConsignmentItem(this.entity, consignmentItemData, named, amount))
        }
    }

    data class ConsignmentItem(
        val itemPrefab: Entity,
        val data: ConsignmentItemData,
        val itemName: Named,
        val amount: Int
    )

    private class ConsignmentItemEntity(world: World, consignmentOrder: Entity) : EntityQueryContext(world) {
        val consignmentItemData: ConsignmentItemData by relation(consignmentOrder)
        val amount by component<Amount?>()
        val named by component<Named>()
    }
}

@Composable
fun MarketView(entity: Entity): Unit = UIEntity(entity, updaterFactory = { MarketViewModel(it, entity) }) {
    Column(modifier = Modifier.fillMaxSize(1f)) {
        Text(it.name)
        val pagerState = rememberPagerState(0) { 2 }
        HorizontalPager(state = pagerState) { index ->
            when (index) {
                0 -> AcquisitionOrderListView(Modifier.weight(1f), it.acquisitionOrder)
                1 -> ConsignmentOrderListView(Modifier.weight(1f), it.consignmentOrder)
            }
        }
        LaunchedEffect(10) {
            while (true) {
                pagerState.scrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
                delay(1000)
            }
        }
    }
}

@Composable
fun ConsignmentOrderListView(modifier: Modifier = Modifier, consignmentOrders: List<Entity>) = LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = modifier) {
    items(consignmentOrders) { ConsignmentOrderView(it) }
}

@Composable
fun ConsignmentOrderView(entity: Entity) = UIEntity(entity, updaterFactory = ::ConsignmentOrderViewModel) {
    Column(Modifier.heightIn(200.dp)) {
        Text("${it.ownerName} 的出售订单")
        Text("Total Value: ${it.totalValue}")
        LazyColumn(modifier = Modifier.weight(1f).heightIn(50.dp)) {
            items(it.consignmentItems) { consignmentItem ->
                Text("${consignmentItem.itemName.name} x ${consignmentItem.amount} - ${consignmentItem.data.unitPrice}")
            }
        }
    }
}

@Composable
fun AcquisitionOrderListView(modifier: Modifier = Modifier, acquisitionOrders: List<Entity>) = LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = modifier) {
    println("AcquisitionOrderListView $acquisitionOrders")
    items(acquisitionOrders) { AcquisitionOrderView(it) }
}

@Composable
fun AcquisitionOrderView(entity: Entity) = UIEntity(entity, updaterFactory = ::AcquisitionOrderViewModel) {
    Column(Modifier.heightIn(200.dp)) {
        Text("${it.ownerName} 的收购订单")
        Text("Total Value: ${it.totalValue}")
        LazyColumn(modifier = Modifier.weight(1f).heightIn(50.dp)) {
            items(it.acquisitionItems) { acquisitionItem ->
                Text("${acquisitionItem.itemName.name} x ${acquisitionItem.data.count} - ${acquisitionItem.data.unitPrice}")
            }
        }
    }
}

@Preview
@Composable
fun MarketPreview(): Unit = MainWorld {
    val world = currentWorld
    val market = remember {
        val marketService by world.di.instance<MarketService>()
        val itemService by world.di.instance<ItemService>()
        val player = world.entity {
            it.addComponent(Named("Player"))
            it.addComponent(Money(10000))
        }
        val market = marketService.createMarket(player, Named("Market"))

        val itemPrefab = itemService.itemPrefabs.first()
        val item = itemService.item(itemPrefab) {
            it.addRelation<OwnedBy>(player)
        }
        marketService.consignItems(market, player) {
            addItem(item, 10, 100)
        }
        marketService.createAcquisitionOrder(market, player) {
            addItem(itemPrefab, 1, 100)
        }
        market
    }
    MarketView(market)
}
