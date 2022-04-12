package com.bancempo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.graphics.Matrix
import android.media.ExifInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.*


class EditProfileActivity : AppCompatActivity() {

    lateinit var fullName : TextView;
    lateinit var photo : ImageView
    lateinit var nickname : TextView
    lateinit var email : TextView
    lateinit var location : TextView
    lateinit var skills : TextView
    lateinit var description : TextView
    lateinit var editPicture : ImageButton

    val REQUEST_IMAGE_CAPTURE = 1
    val SELECT_PICTURE = 200

    lateinit var orientation : String
    var w: Int = 0
    var h: Int = 0

    //var uri_or_bitmap:String = "";
    var bitmap_photo :Bitmap? = null;
    var uri_photo :Uri? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val sv = findViewById<ScrollView>(R.id.sv2)
        val v1 = findViewById<LinearLayout>(R.id.v1)
        val v5 = findViewById<RelativeLayout>(R.id.rl)

        photo = findViewById<ImageView>(R.id.profile_pic)
        editPicture = findViewById<ImageButton>(R.id.changeImageButton)
        fullName = findViewById<TextView>(R.id.editTextFullName)
        nickname = findViewById<TextView>(R.id.editTextNickname)
        email = findViewById<TextView>(R.id.editTextEmail)
        location = findViewById<TextView>(R.id.editTextLocation)
        skills = findViewById<TextView>(R.id.editTextSkills)
        description = findViewById<TextView>(R.id.editTextDescription)

        val orientation: Int = this.resources.configuration.orientation

        if (orientation === Configuration.ORIENTATION_LANDSCAPE ) {
            v1.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    h = v1.height
                    w = v1.width
                    Log.d("Layout", "v1.requestLayout(): $w,$h")
                    v5.post { v5.layoutParams = LinearLayout.LayoutParams(w / 3, h) }
                    v1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

        } else {
            sv.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    h = sv.height
                    w = sv.width
                    Log.d("Layout", "v1.requestLayout(): $w,$h")
                    v5.post { v5.layoutParams = LinearLayout.LayoutParams(w, h / 3) }
                    sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }


        if (savedInstanceState != null) {
            loadImageFromStorage("/data/user/0/com.bancempo/app_imageDir")
            fullName.text = savedInstanceState.getString("full_name");
            nickname.text = savedInstanceState.getString("nickname");
            email.text = savedInstanceState.getString("email");
            location.text = savedInstanceState.getString("location");
            skills.text = savedInstanceState.getString("skills")
            description.text = savedInstanceState.getString("description")

            println("restoring from instance state")
        }

        else{
            val bmp = decodeBase64(intent.getStringExtra("com.bancempo.PHOTO"))
            photo.setImageBitmap(bmp)

            fullName.text = intent.getStringExtra("com.bancempo.FULL_NAME")
            nickname.text = intent.getStringExtra("com.bancempo.NICKNAME")
            email.text = intent.getStringExtra("com.bancempo.EMAIL")
            location.text = intent.getStringExtra("com.bancempo.LOCATION")
            skills.text = intent.getStringExtra("com.bancempo.SKILLS")
            description.text = intent.getStringExtra("com.bancempo.DESCRIPTION")

        }
        editPicture.setOnClickListener {
            showPopup(editPicture)
        }
    }

    @SuppressLint("SdCardPath")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("full_name", fullName.text.toString())
        outState.putString("nickname", nickname.text.toString())
        outState.putString("email", email.text.toString())
        outState.putString("location", location.text.toString())
        outState.putString("skills", skills.text.toString())
        outState.putString("description", description.text.toString())
    }
    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(R.menu.menu_profile_picture, popup.menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.select_image_gallery -> {
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.type = "image/*"
                    // pass the constant to compare it with the returned requestCode
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
                    true
                }
                R.id.use_camera -> {
                    dispatchTakePictureIntent()
                    true
                }
            }
            false
        })

        popup.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("WrongThread")
    override fun onBackPressed() {
        val i = Intent(this, ShowProfileActivity::class.java)

        //ENCODE bitmap
        if (bitmap_photo != null){
            println("EDIT_ BITMAP    ${encodeTobase64(bitmap_photo!!)}")
            i.putExtra("com.bancempo.PHOTO", encodeTobase64(bitmap_photo!!));
        }
        else if (uri_photo != null){
            println("EDIT_ URI    $uri_photo")
            i.putExtra("com.bancempo.PHOTO", uri_photo.toString());
        }

        i.putExtra("com.bancempo.FULL_NAME", findViewById<TextView>(R.id.editTextFullName).text.toString())
        i.putExtra("com.bancempo.NICKNAME", findViewById<TextView>(R.id.editTextNickname).text.toString())
        i.putExtra("com.bancempo.EMAIL", findViewById<TextView>(R.id.editTextEmail).text.toString())
        i.putExtra("com.bancempo.LOCATION", findViewById<TextView>(R.id.editTextLocation).text.toString())
        i.putExtra("com.bancempo.SKILLS", findViewById<TextView>(R.id.editTextSkills).text.toString())
        i.putExtra("com.bancempo.DESCRIPTION", findViewById<TextView>(R.id.editTextDescription).text.toString())

        setResult(Activity.RESULT_OK, i)
        super.onBackPressed()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(applicationContext, R.string.camera_not_available, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            bitmap_photo = data.extras?.get("data") as Bitmap;
            saveToInternalStorage(bitmap_photo!!) + "profile.jpeg";
            findViewById<ImageView>(R.id.profile_pic).setImageBitmap(bitmap_photo)

        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null){
            // Get the url of the image from data
            uri_photo = data.data
            // update the preview image in the layout

            //uri_or_bitmap = "uri"
            val bmp:Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri_photo);

            val ins: InputStream? = applicationContext.contentResolver.openInputStream(uri_photo!!)
            val ei = ExifInterface(ins!!)

            val or = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            val rotatedBitmap: Bitmap = when (or) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bmp, 90f);
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bmp, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bmp, 270f)
                ExifInterface.ORIENTATION_NORMAL -> bmp
                else -> bmp
            }

            saveToInternalStorage(rotatedBitmap) + "profile.jpeg";

            findViewById<ImageView>(R.id.profile_pic).setImageURI(uri_photo)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /*------------------------------------  UTILITIES  -------------------------------------------*/
    fun encodeTobase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val imageEncoded: String = Base64.encodeToString(b, Base64.DEFAULT)
        //Log.d("Image Log:", imageEncoded)
        return imageEncoded
    }

    fun decodeBase64(input: String?): Bitmap? {
        val decodedByte: ByteArray = Base64.decode(input, 0)
        return BitmapFactory
            .decodeByteArray(decodedByte, 0, decodedByte.size)
    }


    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {
        val cw = ContextWrapper(applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory: File = cw.getDir("imageDir", MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "profile.jpeg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null) {
                    fos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.getAbsolutePath()
    }


    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun loadImageFromStorage(path: String) {
        try {
            val f = File(path, "profile.jpeg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            val img = findViewById<ImageView>(R.id.profile_pic)
            img.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

}

