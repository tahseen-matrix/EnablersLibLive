package com.matrix.enablersliblive

/**
 * Created By Matrix Marketers
 */
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CartAdapter(private val context: Context, private val cartItems: List<CartItem>) : BaseAdapter() {

    override fun getCount(): Int = cartItems.size

    override fun getItem(position: Int): Any = cartItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false)

        val itemNameTextView: TextView = view.findViewById(R.id.itemNameTextView)
        val itemPriceTextView: TextView = view.findViewById(R.id.itemPriceTextView)
        val quantityTextView: TextView = view.findViewById(R.id.quantityTextView)

        val cartItem = getItem(position) as CartItem

        itemNameTextView.text = cartItem.itemName
        itemPriceTextView.text = "$${cartItem.itemPrice}"
        quantityTextView.text = "Quantity: ${cartItem.quantity}"

        return view
    }
}
