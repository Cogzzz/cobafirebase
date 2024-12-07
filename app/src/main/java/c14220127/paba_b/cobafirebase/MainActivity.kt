package c14220127.paba_b.cobafirebase

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
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
    lateinit var lvAdapter: ArrayAdapter<daftarProvinsi>
    lateinit var etProvinsi: EditText
    lateinit var etIbukota: EditText
    var data : MutableList<Map<String, Any>> = ArrayList()

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

        lvAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, DataProvinsi)
        lvData.adapter = lvAdapter

        btnSimpan.setOnClickListener {
            TambahData(DB, etProvinsi.text.toString(), etIbukota.text.toString())
        }
    }

    fun TambahData(db: FirebaseFirestore, provinsi: String, ibukota: String) {
        val datBaru = daftarProvinsi(provinsi, ibukota)
        db.collection("tbProvinsi")
            .add(datBaru)
            .addOnSuccessListener {
                etProvinsi.setText("")
                etIbukota.setText("")
                Log.d("Firebase", "Data berhasil Disimpan")
            }

            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
        readData(db)
    }

    fun readData(db: FirebaseFirestore){
        db.collection("tbProvinsi").get()
            .addOnSuccessListener {
                result ->
                DataProvinsi.clear()
                for (document in result){
                    val readData = daftarProvinsi(
                        document.data["provinsi"].toString(),
                        document.data["ibukota"].toString()
                    )
                    DataProvinsi.add(readData)
                }
                lvAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener{
                Log.d("Firebase", it.message.toString())
            }
    }
}