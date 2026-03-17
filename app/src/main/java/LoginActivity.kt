package com.example.poctomatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var employeeDao: EmployeeDao
    private lateinit var cellDao: CellDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = AppDatabase.getDatabase(this)
        Toast.makeText(this, db.openHelper.writableDatabase.path, Toast.LENGTH_LONG).show()
        employeeDao = db.employeeDao()
        cellDao = db.cellDao()

        insertInitialData()
        if (cellDao.getCount() == 0) {
            val cells = mutableListOf<CellEntity>()

            for (postamatId in 1..2) {
                for (number in 1..32) {

                    val size = when (number) {
                        in 1..8 -> "S"
                        in 9..16 -> "M"
                        in 17..24 -> "L"
                        else -> "XL"
                    }

                    cells.add(
                        CellEntity(
                            postamatId = postamatId,
                            number = number,
                            size = size
                        )
                    )
                }
            }

            cellDao.insertAll(cells)
        }

        val editLogin = findViewById<EditText>(R.id.editLogin)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val login = editLogin.text.toString().trim()
            val password = editPassword.text.toString().trim()

            val employee = employeeDao.login(login, password)

            if (employee != null) {
                val intent = Intent(this, PostamatSelectActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun insertInitialData() {
        if (employeeDao.getCount() == 0) {
            employeeDao.insert(
                EmployeeEntity(
                    login = "admin",
                    password = "1234",
                    fullName = "Администратор"
                )
            )
        }

        if (cellDao.getCount() == 0) {
            val cells = mutableListOf<CellEntity>()

            for (postamatId in 1..2) {
                for (number in 1..32) {

                    val size = when (number) {
                        in 1..8 -> "S"
                        in 9..16 -> "M"
                        in 17..24 -> "L"
                        else -> "XL"
                    }

                    cells.add(
                        CellEntity(
                            postamatId = postamatId,
                            number = number,
                            size = size
                        )
                    )
                }
            }

            cellDao.insertAll(cells)
        }
    }
}
