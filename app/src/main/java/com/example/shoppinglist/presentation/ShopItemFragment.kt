package com.example.shoppinglist.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.FragmentShopItemBinding
import com.example.shoppinglist.domain.ShopItem
import javax.inject.Inject

class ShopItemFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val component by lazy {
        (requireActivity().application as ShopListApplication).component
    }

    private lateinit var shopItemViewModel: ShopItemViewModel
    private lateinit var onShopItemEditingFinishedListener: OnShopItemEditingFinishedListener

    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding == null")

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)

        if (context is OnShopItemEditingFinishedListener) {
            onShopItemEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnShopItemEditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shopItemViewModel = ViewModelProvider(this, viewModelFactory)[ShopItemViewModel::class.java]

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
        shopItemViewModel.errorInputCount.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_count)
            } else {
                null
            }

            binding.countTil.error = message
        }
        shopItemViewModel.errorInputName.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_name)
            } else {
                null
            }

            binding.nameTil.error = message
        }
        shopItemViewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

    }

    private fun setupTextChangeListener() {
        binding.nameTiet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                shopItemViewModel.resetErrorInputName()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.countTiet.addTextChangedListener(object : TextWatcher {
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
        shopItemViewModel.shopItem.observe(viewLifecycleOwner) {
            binding.nameTiet.setText(it.name)
            binding.countTiet.setText(it.count.toString())
        }

        binding.saveButton.setOnClickListener {
            shopItemViewModel.editShopItem(
                binding.nameTiet.text?.toString(),
                binding.countTiet.text?.toString()
            )
        }
    }

    private fun launchAddMode() {
        binding.saveButton.setOnClickListener {
            shopItemViewModel.addShopItem(
                binding.nameTiet.text?.toString(),
                binding.countTiet.text?.toString()
            )
        }
    }

    private fun parseParams() {
        val args = requireArguments()

        if (!args.containsKey(KEY_SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }

        val mode = args.getString(KEY_SCREEN_MODE)

        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode $mode, Fragment parse")
        }

        screenMode = mode

        if (screenMode == MODE_EDIT && !args.containsKey(KEY_SHOP_ITEM_ID)) {
            throw RuntimeException("Param ShopItemId is absent")
        }

        shopItemId = args.getInt(KEY_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
    }

    interface OnShopItemEditingFinishedListener {

        fun onShopItemEditingFinished()
    }

    companion object {
        private const val KEY_SCREEN_MODE = "extra_mode"
        private const val KEY_SHOP_ITEM_ID = "extra_KEY_SHOP_ITEM_ID"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""


        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_SCREEN_MODE, MODE_EDIT)
                    putInt(KEY_SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }
}