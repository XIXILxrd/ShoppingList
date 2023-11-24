package com.example.shoppinglist.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ShopItemFragment.OnShopItemEditingFinishedListener {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (application as ShopListApplication).component
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupRecyclerView()

        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModel.shopList.observe(this) {
            shopListAdapter.submitList(it)
        }

        binding.addItemShopButton.setOnClickListener {
            if (isLandscapeOrientation()) {
                launchFragment(ShopItemFragment.newInstanceAddItem())
            } else {
                val intent = ShopItemActivity.newIntentAddItem(this)

                startActivity(intent)
            }
        }
    }

    private fun isLandscapeOrientation(): Boolean {
        return binding.shopItemContainer != null
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerView() {

        shopListAdapter = ShopListAdapter()
        with(binding.shopListRv) {
            adapter = shopListAdapter

            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_ENABLED,
                ShopListAdapter.MAX_POOL_SIZE
            )

            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_DISABLED,
                ShopListAdapter.MAX_POOL_SIZE
            )
        }

        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(binding.shopListRv)
    }

    private fun setupSwipeListener(shopListRecycleView: RecyclerView?) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = shopListAdapter.currentList[viewHolder.adapterPosition]

                viewModel.deleteShopItem(item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(shopListRecycleView)
    }

    private fun setupClickListener() {
        shopListAdapter.onShopItemClickListener = {
            if (isLandscapeOrientation()) {
                launchFragment(ShopItemFragment.newInstanceEditItem(it.id))
            } else {
                val intent = ShopItemActivity.newIntentEditItem(this, it.id)

                startActivity(intent)
            }
        }
    }

    private fun setupLongClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }

    override fun onShopItemEditingFinished() {
        Toast.makeText(this, R.string.success_toast, Toast.LENGTH_SHORT).show()
        supportFragmentManager.popBackStack()
    }
}
