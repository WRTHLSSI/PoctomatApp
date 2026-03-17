package com.example.poctomatapp

import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import android.widget.ArrayAdapter

import android.widget.Spinner


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CellAdapter
    private lateinit var db: AppDatabase
    private lateinit var cellDao: CellDao
    private var postamatId: Int = 1
    private var cells: MutableList<CellEntity> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postamatId = intent.getIntExtra("POSTAMAT_ID", 1)

        val titleText = findViewById<TextView>(R.id.titleText)
        titleText.text = "Почтомат $postamatId"

        db = AppDatabase.getDatabase(this)
        cellDao = db.cellDao()

        recyclerView = findViewById(R.id.cellGrid)
        recyclerView.layoutManager = GridLayoutManager(this, 4)

        loadCellsFromDatabase()

        adapter = CellAdapter(cells) { cell ->
            onCellClicked(cell)
        }

        recyclerView.adapter = adapter

        val searchTrackInput = findViewById<EditText>(R.id.searchTrackInput)
        val searchButton = findViewById<Button>(R.id.searchButton)

        searchButton.setOnClickListener {
            val track = searchTrackInput.text.toString().trim()

            if (track.isEmpty()) {
                Toast.makeText(this, "Введите трек-номер", Toast.LENGTH_SHORT).show()
            } else {
                searchParcel(track)
            }
        }
    }

    private fun loadCellsFromDatabase() {
        cells = cellDao.getCellsByPostamat(postamatId).toMutableList()
    }

    private fun refreshCells() {
        cells = cellDao.getCellsByPostamat(postamatId).toMutableList()
        adapter.updateData(cells)
    }

    private fun onCellClicked(cell: CellEntity) {
        if (cell.isOccupied) {
            showOccupiedCellDialog(cell)
        } else {
            showEmptyCellDialog(cell)
        }
    }

    private fun showEmptyCellDialog(cell: CellEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_parcel, null)

        val trackInput = dialogView.findViewById<EditText>(R.id.trackInput)
        val sizeSpinner = dialogView.findViewById<Spinner>(R.id.sizeSpinner)

        trackInput.filters = arrayOf(InputFilter.LengthFilter(12))

        val sizes = arrayOf("S", "M", "L", "XL")
        val adapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            sizes
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sizeSpinner.adapter = adapterSpinner

        AlertDialog.Builder(this)
            .setTitle("Положить посылку")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val track = trackInput.text.toString().trim()
                val selectedSize = sizeSpinner.selectedItem.toString()

                if (track.length != 12) {
                    Toast.makeText(
                        this,
                        "Неправильный трек-номер, попробуйте еще раз",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                if (selectedSize != cell.size) {
                    Toast.makeText(
                        this,
                        "Размер посылки не подходит для этой ячейки",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val updatedCell = cell.copy(
                    isOccupied = true,
                    trackNumber = track,
                    expiryDate = calculateExpiryDate()
                )

                cellDao.updateCell(updatedCell)
                refreshCells()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showOccupiedCellDialog(cell: CellEntity) {
        val expiredText = if (isExpired(cell.expiryDate)) {
            "\nСтатус: срок хранения истёк"
        } else {
            ""
        }

        val message = """
            Ячейка ${cell.number} занята
            
            Трек-номер: ${cell.trackNumber}
            Срок хранения до: ${cell.expiryDate}$expiredText
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Информация о посылке")
            .setMessage(message)
            .setPositiveButton("Выдать посылку") { _, _ ->
                val updatedCell = cell.copy(
                    isOccupied = false,
                    trackNumber = null,
                    expiryDate = null
                )

                cellDao.updateCell(updatedCell)
                refreshCells()
            }
            .setNegativeButton("Закрыть", null)
            .show()
    }

    private fun searchParcel(trackNumber: String) {
        val cell = cellDao.findByTrackNumber(trackNumber)

        if (cell == null) {
            Toast.makeText(this, "Посылка не найдена", Toast.LENGTH_SHORT).show()
        } else {
            val expiredText = if (isExpired(cell.expiryDate)) {
                "\nСтатус: срок хранения истёк"
            } else {
                ""
            }

            val message = """
                Посылка найдена
                
                Почтомат: ${cell.postamatId}
                Ячейка: ${cell.number}
                Трек-номер: ${cell.trackNumber}
                Срок хранения до: ${cell.expiryDate}$expiredText
            """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle("Результат поиска")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun calculateExpiryDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun isExpired(expiryDate: String?): Boolean {
        if (expiryDate.isNullOrEmpty()) return false

        return try {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val expiry = format.parse(expiryDate)
            val today = format.parse(format.format(Date()))
            expiry != null && today != null && today.after(expiry)
        } catch (e: Exception) {
            false
        }
    }
}