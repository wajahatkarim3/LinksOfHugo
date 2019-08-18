package com.wajahatkarim3.linksofhugo

import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import com.nguyencse.URLEmbeddedData
import com.nguyencse.URLEmbeddedView
import com.wajahatkarim3.linksofhugo.databinding.ActivityMainBinding
import android.content.Intent
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import android.content.Context.CLIPBOARD_SERVICE





class MainActivity : AppCompatActivity() {

    lateinit var bi: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupViews()

        //get the received intent
        val receivedIntent = intent
        var receivedAction = receivedIntent.action
        val receivedType = receivedIntent.type
        if (receivedAction == Intent.ACTION_SEND)
        {
            // Get Shared Content
            var text = receivedIntent.getStringExtra(Intent.EXTRA_TEXT)
            // Extract URL from text
            var possibleUrl = text.substring(text.indexOf("http"))
            if (possibleUrl.contains(" "))
            {
                var url = possibleUrl.substring(0, possibleUrl.indexOf(" "))
                bi.txtLink.setText(url)
                performUrlFetching()
            }
            else
            {
                bi.txtLink.setText(possibleUrl)
                performUrlFetching()
            }
        }
    }

    fun setupViews()
    {
        bi.txtLink.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                performUrlFetching()
                true
            }
            false
        }

        bi.btnCopy.setOnClickListener {
            val cm = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setText(bi.txtHugoCode.text.toString())
        }

        bi.btnShare.setOnClickListener {
            var intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, bi.txtHugoCode.text.toString())
            intent.setType("text/plain")
            startActivity(intent)
        }
    }

    fun performUrlFetching()
    {
        bi.urlEmbeddedView.setURL(bi.txtLink.text.toString())
        {
            bi.urlEmbeddedView.apply {
                title(it.title)
                description(it.description)
                host(it.host)
                thumbnail(it.thumbnailURL)
                favor(it.favorURL)
            }

            generateHugoCode(it)
        }
    }

    private fun generateHugoCode(linkData: URLEmbeddedData?) {
        var codeStr = getString(R.string.hugo_template_str,
                linkData?.title,
                linkData?.description,
                bi.txtLink.text.toString(),
                linkData?.thumbnailURL)
        bi.txtHugoCode.text = codeStr
    }
}
