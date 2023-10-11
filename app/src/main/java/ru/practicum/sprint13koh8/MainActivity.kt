package ru.practicum.sprint13koh8

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.practicum.sprint13koh8.databinding.ActivityMainBinding
import java.util.UUID


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SPRINT_13"
    }

    val serverApi: ServerApi by lazy {
        ServerApi.create()
    }

    // UI
    private lateinit var binding: ActivityMainBinding
    private lateinit var badge: BadgeDrawable
    private val catalogItemsAdapter: CatalogItemsAdapter by lazy {
        CatalogItemsAdapter()
    }
    private val cartItemsAdapter: CartItemsAdapter by lazy {
        CartItemsAdapter()
    }

    // Logic
    private var currentScreenMode: ScreenMode? = null
    var catalogItems = emptyList<CatalogItemViewData>()
    var cartItems = emptyList<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        changeCurrentScreenMode(ScreenMode.CATALOG)
        binding.toolbar.setTitle(R.string.catalog_title)
        binding.bottomNavigation.selectedItemId = R.id.catalog
        binding.bottomNavigation.setOnItemSelectedListener {
            onBottomNavigationItemSelected(it.itemId)
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        badge =
            bottomNavigationView.getOrCreateBadge(R.id.cart) // ID вашего элемента нижней навигации
        badge.isVisible = false

        setUpCatalog()
        setUpCart()

        serverApi.getCatalog()
            .enqueue(object : Callback<CatalogResponse> {
                override fun onResponse(
                    call: Call<CatalogResponse>,
                    response: Response<CatalogResponse>
                ) {
                    val body = response.body()
                    if (response.code() == 200 && body != null) {
                        catalogItems = body.items.map {
                            CatalogItemViewData(
                                item = it,
                                count = null
                            )
                        }
                        catalogItemsAdapter.setItems(catalogItems)
                    } else {
                        Log.d(TAG, "onResponse: error call=$call body=$body")
                    }
                }

                override fun onFailure(call: Call<CatalogResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: $call $t")
                }

            })
    }

    private fun setUpCatalog() {
        binding.catalogItemsList.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = catalogItemsAdapter
            itemAnimator = null
        }

        catalogItemsAdapter.setItems(catalogItems)
        with(catalogItemsAdapter) {
            onAddToCartClickListener = OnAddToCartClickListener { item ->
                catalogItems = catalogItems.map {
                    if (it.id == item.id) {
                        cartItems = cartItems.toMutableList().apply {
                            add(
                                CartItem(
                                    id = UUID.randomUUID().toString(),
                                    catalogItem = it.item,
                                    count = 1
                                )
                            )
                        }
                        cartItemsAdapter.setItems(cartItems)
                        changeVisibilityOfEmptyBasketTitle()
                        it.copy(count = 1)
                    } else {
                        it
                    }
                }
                setBudgeCount()
                catalogItemsAdapter.setItems(catalogItems)
            }
            onAddCountClickListener = OnAddCountClickListener { item ->
                catalogItems = catalogItems.map {
                    if (it.id == item.id) {
                        cartItems = cartItems.map {
                            if (it.catalogItem.id == item.id) {
                                it.copy(count = it.count + 1)
                            } else {
                                it
                            }
                        }


                        it.copy(count = (it.count ?: 0) + 1)
                    } else {
                        it
                    }
                }
                catalogItemsAdapter.setItems(catalogItems)
                cartItemsAdapter.setItems(cartItems)
            }
            onRemoveCountClickListener = OnRemoveCountClickListener { item ->
                catalogItems = catalogItems.map {
                    if (it.id == item.id) {
                        if (item.count == 1) {
                            cartItems = cartItems.toMutableList().apply {
                                remove(
                                    cartItems.find { it.catalogItem.id == item.id }
                                )
                            }
                        }
                        cartItems = cartItems.map {
                            if (it.catalogItem.id == item.id) {
                                it.copy(count = it.count - 1)
                            } else {
                                it
                            }
                        }
                        it.copy(count = (it.count ?: 0) - 1)

                    } else {
                        it
                    }
                }
                setBudgeCount()
                cartItemsAdapter.setItems(cartItems)
                catalogItemsAdapter.setItems(catalogItems)
                changeVisibilityOfEmptyBasketTitle()
            }
        }
    }

    private fun setBudgeCount() {
        if (cartItems.isNotEmpty()) {
            badge.isVisible = true
            badge.number = cartItems.size
        } else {
            badge.isVisible = false
        }
    }

    private fun setUpCart() {
        changeVisibilityOfEmptyBasketTitle()
        binding.cartItemsList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cartItemsAdapter
            itemAnimator = null
        }

        cartItemsAdapter.setItems(cartItems)
        with(cartItemsAdapter) {
            onAddCountClickListener = OnCartAddCountClickListener { item ->
                cartItems = cartItems.map {
                    if (it.id == item.id) {
                        catalogItems = catalogItems.map {
                            if (it.id == item.catalogItem.id) {
                                it.copy(count = (it.count ?: 0) + 1)
                            } else {
                                it
                            }
                        }
                        it.copy(count = it.count + 1)
                    } else {
                        it
                    }
                }
                cartItemsAdapter.setItems(cartItems)
                catalogItemsAdapter.setItems(catalogItems)
            }
            onRemoveCountClickListener = OnCartRemoveCountClickListener { item ->
                cartItems = cartItems.mapNotNull {
                    if (it.id == item.id) {


                        catalogItems = catalogItems.map {
                            if (it.id == item.catalogItem.id) {
                                it.copy(count = (it.count ?: 0) - 1)
                            } else {
                                it
                            }
                        }
                        if (it.count == 1) {
                            null
                        } else {
                            it.copy(count = it.count - 1)
                        }
                    } else {
                        it
                    }
                }
                setBudgeCount()
                cartItemsAdapter.setItems(cartItems)
                catalogItemsAdapter.setItems(catalogItems)
                changeVisibilityOfEmptyBasketTitle()
            }
        }
    }

    private fun onBottomNavigationItemSelected(itemId: Int): Boolean {
        return when (itemId) {
            R.id.catalog -> {
                changeCurrentScreenMode(ScreenMode.CATALOG)
                true
            }

            R.id.cart -> {
                changeCurrentScreenMode(ScreenMode.CART)
                true
            }

            else -> false
        }
    }

    private fun changeVisibilityOfEmptyBasketTitle() {
        if (cartItems.isEmpty()) {
            binding.cartEmptyTitle.visibility = View.VISIBLE
        } else {
            binding.cartEmptyTitle.visibility = View.GONE
        }
    }

    private fun changeCurrentScreenMode(newScreenMode: ScreenMode) {
        if (newScreenMode != currentScreenMode) {
            when (newScreenMode) {
                ScreenMode.CATALOG -> {
                    binding.toolbar.setTitle(R.string.catalog_title)
                    binding.catalogContainer.visibility = View.VISIBLE
                    binding.cartContainer.visibility = View.GONE
                }

                ScreenMode.CART -> {
                    binding.toolbar.setTitle(R.string.cart_title)
                    binding.catalogContainer.visibility = View.GONE
                    binding.cartContainer.visibility = View.VISIBLE
                }
            }
            currentScreenMode = newScreenMode
        }
    }
}