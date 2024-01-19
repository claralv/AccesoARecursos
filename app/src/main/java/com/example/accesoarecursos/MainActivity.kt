package com.example.accesoarecursos
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ImageButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.accesoarecursos.databinding.ActivityMainBinding
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity: AppCompatActivity() {
    lateinit var imagen: ImageButton
    lateinit var binding: ActivityMainBinding


    private fun guardarImagenEnGaleria(image: Bitmap) {
        // Obtener el URI de la imagen en la galería
        val imageUri = insertarImagenEnGaleria(image, "Imagen desde la Cámara", "Descripción")

        // Verificar que el URI no sea nulo
        if (imageUri != null) {
            // Notificar a la galería que se ha añadido una nueva imagen
            Log.d("Ruta de archivo", imageUri.toString())
            MediaScannerConnection.scanFile(
                this,
                arrayOf(imageUri.toString()),
                null
            ) { _, uri ->
                // Puedes manejar el resultado aquí si es necesario
            }
        } else {
            Log.e("Error", "El URI de la imagen es nulo.")
        }
    }

    private fun insertarImagenEnGaleria(image: Bitmap, s: String, s1: String): Uri? {
        // Obtener la ubicación de almacenamiento externo pública
        val imagesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)


        // Crear un archivo para la imagen
        val imagen = File(imagesDir, "$title.jpg")

        // Guardar la imagen en el archivo
        try {
            FileOutputStream(imagen).use { fos ->
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Insertar la imagen en la galería
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, s)
            put(MediaStore.Images.Media.DESCRIPTION, s1)
            put(MediaStore.Images.Media.DATA, imagen.absolutePath)
        }

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    }



    //pickMedia maneja el resultado de una actividad lanzada para seleccionar una imagen de la galería.
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        //Devuelve la uri de la imagen seleccionada:
        uri ->
        if(uri!=null) { //Si la uri de la imagen seleccionada no es nula
            //asigna la uri a nuestro imageButton
            imagen.setImageURI(uri)
        }else {
            //no se ha seleccionado ninguna imagen
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imagen=binding.imageButton

        //pickFoto es un lanzador que maneja el resultado de capturar una imagen desde la cámara.
        val pickFoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //obtenemos un objeto bitmap de los extras del resultado y lo almacenamos en la variable image
            val image = it.data?.extras?.get("data") as Bitmap
            //hacemos que la imagen se muestre en nuestro imageButton
            binding.imageButton.setImageBitmap(image)

            guardarImagenEnGaleria(image)
        }

        //Cuando pulsemos sobre el imageButton, llamaremos al launcher (pickMedia) para lanzarlo(galería)
        imagen.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //Cuando pulsemos sobre el botón ACCEDER A LA CÁMARA, llamamos al launcher (pickFoto) para lanzarlo:
        binding.button2.setOnClickListener {
            pickFoto.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
    }
 }





