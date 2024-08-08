package com.infinity8.truecallermirror.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infinity8.truecallermirror.controller.UICallback
import com.infinity8.truecallermirror.databinding.ContactItemBinding
import com.infinity8.truecallermirror.model.Contacts
import com.infinity8.truecallermirror.uitls.diff.DiffCallback

internal class ContactsAdapter(val context: Context, val uiCallback: UICallback) :
    PagingDataAdapter<Contacts, ContactsAdapter.ContactViewHolder>(
        DiffCallback()
    ) {
    inner class ContactViewHolder(private val binding: ContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contacts: Contacts?) {
            binding.contactName.text = contacts?.name
            binding.contactPhoneNumber.text = contacts?.number
            binding.prefixIcon.text = contacts?.name?.first().toString()
        }
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContactViewHolder(
            ContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
}