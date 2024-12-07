package c14220127.paba_b.cobafirebase

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    val DB = Firebase.firestore
    var DataProvinsi = ArrayList<daftarProvinsi>()
    lateinit var lvAdapter: SimpleAdapter
    lateinit var etProvinsi: EditText
    lateinit var etIbukota: EditText
    var data: MutableList<Map<String, Any>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etProvinsi = findViewById<EditText>(R.id.etProvinsi)
        etIbukota = findViewById<EditText>(R.id.etIbukota)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val lvData = findViewById<ListView>(R.id.lvData)

        lvData.setOnItemLongClickListener { parent, view, position, id ->
            val namaPro = data[position].get("Pro")
            if (namaPro != null) {
                DB.collection("tbProvinsi")
                    .document(namaPro.toString())
                    .delete()
                    .addOnSuccessListener {
                        Log.d("Firebase", "Data berhasil Dihapus")
                        readData(DB)
            }
                .addOnFailureListener {
                    Log.w("Firebase", it.message.toString())
                }
            }
            true
        }


        lvAdapter = SimpleAdapter(
            this, data, android.R.layout.simple_list_item_2, arrayOf("Pro", "Ibu"), intArrayOf(
                android.R.id.text1, android.R.id.text2
            )
        )
        lvData.adapter = lvAdapter

        btnSimpan.setOnClickListener {
            TambahData(DB, etProvinsi.text.toString(), etIbukota.text.toString())
        }
    }

    fun TambahData(db: FirebaseFirestore, provinsi: String, ibukota: String) {
        val dataBaru = daftarProvinsi(provinsi, ibukota)
        db.collection("tbProvinsi")
            .document(dataBaru.provinsi)
            .set(dataBaru)
            .addOnSuccessListener {
                etProvinsi.setText("")
                etIbukota.setText("")
                Log.d("Firebase", "Data berhasil Disimpan")
                readData(db)
            }

            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
        readData(db)
    }

    fun readData(db: FirebaseFirestore) {
        db.collection("tbProvinsi").get()
            .addOnSuccessListener { result ->
                DataProvinsi.clear()
                for (document in result) {
                    val readData = daftarProvinsi(
                        document.data["provinsi"].toString(),
                        document.data["ibukota"].toString()
                    )
                    DataProvinsi.add(readData)
                    data.clear()
                    DataProvinsi.forEach {
                        val dt: MutableMap<String, String> = HashMap(2)
                        dt["Pro"] = it.provinsi
                        dt["Ibu"] = it.ibukota
                        data.add(dt)
                    }
                }
                lvAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }
}
