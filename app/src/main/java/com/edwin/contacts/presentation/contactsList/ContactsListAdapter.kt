package com.edwin.contacts.presentation.contactsList

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edwin.contacts.databinding.ContactsListItemBinding
import com.edwin.contacts.presentation.GlideImageLoader
import com.edwin.domain.model.Contact

class ContactsListAdapter(private val onClick: (Contact) -> Unit) :
    ListAdapter<Contact, ContactsListAdapter.ContactsListViewHolder>(ContactDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsListViewHolder {
        val binding = ContactsListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsListViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)
    }

    inner class ContactsListViewHolder(private val binding: ContactsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onClick(getItem(adapterPosition)) }
        }

        fun bind(contact: Contact) = with(binding) {
            firstName.text = contact.firstName
            lastName.text = contact.lastName
            GlideImageLoader()
                .loadImage(contactImage.context, contactImage, Uri.parse(contact.avatarPath))
        }

    }

    object ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact) =
            oldItem.phoneNumber == newItem.phoneNumber

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact) = oldItem == newItem
    }
}