package com.example.tetris

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tetris.databinding.ActivityRecordBinding
import com.example.tetris.logic.local.Database.TetrisDatabase
import com.example.tetris.logic.local.Entity.GameRecord
import com.example.tetris.ui.adapter.RecordAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

//做记录
class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding
    private lateinit var adapter: RecordAdapter
    private val db by lazy { TetrisDatabase.getInstance(this) }
    private var recordsJob: Job? = null
    private var currentDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolBarBtnBack()
        setupRecyclerView()
        setSortSpinner()
        loadRecords(0)
    }

    private fun loadRecords(sortIndex: Int) {
        recordsJob?.cancel()
        recordsJob = lifecycleScope.launch {
            val flow = when (sortIndex){
                0 ->db.gameRecordDao().getAllOrderByDate()
                1 ->db.gameRecordDao().getAllOrderByScore()
                else -> db.gameRecordDao().getAllOrderByDate()
            }
            flow.collect{   records->
                adapter.submitList(records)
                binding.tvRecordCount.text = "共计${records.size}条记录"
            }
        }

    }
    private fun setSortSpinner(){
        binding.spinnerSort.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadRecords(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
    }

    // 返回之间销毁activity
    private fun toolBarBtnBack(){
        binding.btnBack.setOnClickListener{
            finish()
        }
    }
    // RecyclerView
    private fun setupRecyclerView() {
        adapter = RecordAdapter(onNoteClick = { record -> showEditNoteDialog(record) })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    // 显示弹窗编辑备注
    private fun showEditNoteDialog(record: GameRecord) {
        currentDialog?.dismiss()
        val editText = EditText(this).apply {
            setText(record.note)
            setSelection(record.note.length)
            hint = "请输入备注"
        }
        currentDialog = AlertDialog.Builder(this)
            .setTitle("编辑备注")
            .setView(editText)
            .setPositiveButton("确定") { _, _ ->
                val newNote = editText.text.toString().trim()
                if (newNote != record.note) {
                    lifecycleScope.launch {
                        db.gameRecordDao().update(record.copy(note = newNote))
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    override fun onDestroy() {
        recordsJob?.cancel()
        currentDialog?.dismiss()
        super.onDestroy()
    }
}