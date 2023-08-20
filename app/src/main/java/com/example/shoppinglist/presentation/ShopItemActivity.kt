package com.example.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputLayout

class ShopItemActivity : AppCompatActivity() {

    private lateinit var shopItemViewModel: ShopItemViewModel

    private lateinit var nameTextInputLayout: TextInputLayout
    private lateinit var countTextInputLayout: TextInputLayout
    private lateinit var nameEditText: EditText
    private lateinit var countEditText: EditText
    private lateinit var saveButton: Button

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)

        parseIntent()

        shopItemViewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]

        initViews()
        chooseLaunchMode()
        setupTextChangeListener()
        observeViewModel()
    }

    private fun chooseLaunchMode() {
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun observeViewModel() {
        shopItemViewModel.errorInputCount.observe(this) {
            val message = if (it) {
                getString(R.string.error_input_count)
            } else {
                null
            }

            countTextInputLayout.error = message
        }
        shopItemViewModel.errorInputName.observe(this) {
            val message = if (it) {
                getString(R.string.error_input_name)
            } else {
                null
            }

            nameTextInputLayout.error = message
        }
        shopItemViewModel.shouldCloseScreen.observe(this) {
            finish()
        }

    }

    private fun setupTextChangeListener() {
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                shopItemViewModel.resetErrorInputName()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        countEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                shopItemViewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun launchEditMode() {
        shopItemViewModel.getShopItem(shopItemId)
        shopItemViewModel.shopItem.observe(this) {
            nameEditText.setText(it.name)
            countEditText.setText(it.count.toString())
        }

        saveButton.setOnClickListener {
            shopItemViewModel.editShopItem(
                nameEditText.text?.toString(),
                countEditText.text?.toString()
            )
        }
    }

    private fun launchAddMode() {
        saveButton.setOnClickListener {
            shopItemViewModel.addShopItem(
                nameEditText.text?.toString(),
                countEditText.text?.toString()
            )
        }
    }

    private fun initViews() {
        nameTextInputLayout = findViewById(R.id.name_til)
        countTextInputLayout = findViewById(R.id.count_til)
        nameEditText = findViewById(R.id.name_tiet)
        countEditText = findViewById(R.id.count_tiet)
        saveButton = findViewById(R.id.save_button)
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Extra ScreenMode  is absent ")
        }

        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Unknown ScreenMode: $mode")
        }

        screenMode = mode

        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Extra ShopItemId is absent")
            }

            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, -1)
        }
    }

    companion object {
        private const val MODE_UNKNOWN = ""

        private const val EXTRA_SCREEN_MODE = "extra_mode"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)

            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)

            return intent
        }

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)

            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)

            return intent
        }
    }
}