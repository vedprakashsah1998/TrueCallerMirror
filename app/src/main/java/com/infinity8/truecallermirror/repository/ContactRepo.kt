package com.infinity8.truecallermirror.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.infinity8.truecallermirror.dao.ContactDao
import com.infinity8.truecallermirror.datasource.ContactPagingSource
import com.infinity8.truecallermirror.model.Contacts
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactRepo @Inject constructor(@ApplicationContext val context: Context, private val contactDao: ContactDao) {

  /*  suspend fun insertContactList(){
        getAllContacts().forEach { contacts->
            contactDao.insertContacts(contacts)
        }
    }*/
    fun getContacts(): Flow<PagingData<Contacts>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                jumpThreshold = 20,
                prefetchDistance = 20,
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ContactPagingSource(contactDao) }
        ).flow
    }
     fun getContactNames(): List<Pair<Long, String>> {
        val contactProjection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        )

        val contactList = mutableListOf<Pair<Long, String>>()

        val cursor = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            contactProjection,
            null,
            null,
            null
        )

        cursor?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)

                if (name != null) {
                    contactList.add(id to name)
                }
            }
        }

        return contactList
    }
    fun getPhoneNumber(contactId: Long): List<String> {
        val phoneNumberList = mutableListOf<String>()

        val phoneProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val phoneCursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            phoneProjection,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null
        )

        phoneCursor?.use { cursor ->
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val phoneNumber = cursor.getString(numberIndex)
                phoneNumber?.let { phoneNumberList.add(it) }
            }
        }

        return phoneNumberList
    }
    suspend fun insertContacts() {
        // Fetch contact names and IDs
    /*    val contactList = getContactNames()

        contactList.forEach { (id, name) ->
            // Fetch phone numbers for each contact
            val phoneNumbers = getPhoneNumber(id)

            // Insert each contact with their phone numbers into the database
            phoneNumbers.forEach { phoneNumber ->
                contactDao.insertContacts(Contacts(id.toString(), name, phoneNumber))
            }
        }*/
        val list = getNamePhoneDetails()
        list.distinctBy { it.number }.forEach { println(it) }
        contactDao.insertContacts(list)

    }

    @SuppressLint("Range")
    fun getNamePhoneDetails(): MutableList<Contacts> {
        val names :MutableList<Contacts> = mutableListOf()
        val cr = context.contentResolver
        val cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            null, null, null)
        if (cur!!.count > 0) {
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                names.add(Contacts(id , name , number))
            }
        }
        return names
    }
//     suspend fun insertContacts() {
//
//        // Query to get basic contact information
//        val contactProjection = arrayOf(
//            ContactsContract.Contacts._ID,
//            ContactsContract.Contacts.DISPLAY_NAME
//        )
//
//        val cursor = context.contentResolver.query(
//            ContactsContract.Contacts.CONTENT_URI,
//            contactProjection,
//            null,
//            null,
//            null
//        )
//
//        cursor?.use { cursor ->
//            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
//            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
//
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(idIndex)
//                val name = cursor.getString(nameIndex)
//                val phoneNumber = getPhoneNumber(id)
//                if (id != null && name != null && phoneNumber != null){
//                    contactDao.insertContacts(Contacts(id, name, phoneNumber.toString()))
//                }
//
//            }
//        }
//
//    }



}