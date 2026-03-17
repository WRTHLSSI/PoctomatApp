package com.example.poctomatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PostamatSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postamat_select)

        val buttonPostamat1 = findViewById<Button>(R.id.buttonPostamat1)
        val buttonPostamat2 = findViewById<Button>(R.id.buttonPostamat2)

        buttonPostamat1.setOnClickListener {
            openPostamat(1)
        }

        buttonPostamat2.setOnClickListener {
            openPostamat(2)
        }
    }

    private fun openPostamat(postamatId: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("POSTAMAT_ID", postamatId)
        startActivity(intent)
    }
}