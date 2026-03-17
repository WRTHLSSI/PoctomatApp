package com.example.poctomatapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CellDao {

    @Query("SELECT * FROM cells WHERE postamatId = :postamatId ORDER BY number ASC")
    fun getCellsByPostamat(postamatId: Int): List<CellEntity>

    @Insert
    fun insertAll(cells: List<CellEntity>)

    @Update
    fun updateCell(cell: CellEntity)

    @Query("SELECT COUNT(*) FROM cells")
    fun getCount(): Int

    @Query("SELECT * FROM cells WHERE id = :cellId LIMIT 1")
    fun getCellById(cellId: Int): CellEntity?

    @Query("SELECT * FROM cells WHERE trackNumber = :trackNumber LIMIT 1")
    fun findByTrackNumber(trackNumber: String): CellEntity?
}