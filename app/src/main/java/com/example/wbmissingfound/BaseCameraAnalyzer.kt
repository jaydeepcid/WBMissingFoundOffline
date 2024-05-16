package com.example.wbmissingfound

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import com.google.mlkit.vision.common.internal.ImageConvertUtils;

abstract class BaseCameraAnalyzer<T : List<Face>> : ImageAnalysis.Analyzer {

    abstract val graphicOverlay : GraphicOverlay<*>

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        // If the image contains haze, don't process it further

//        if (isImageProper(imageProxy)) {
           // Toast.makeText(applica, " Image is Haze ", Toast.LENGTH_SHORT).show()
            Log.d("UDC-->","IMAGE IS PROPER")
            val mediaImage = imageProxy.image
            mediaImage?.let { image ->
                detectInImage(InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees))
                    .addOnSuccessListener { results ->
                        onSuccess(results, graphicOverlay, image.cropRect)
                        imageProxy.close()
                    }
                    .addOnFailureListener {
                        onFailure(it)
                        imageProxy.close()
                    }
            }
          // Close the image proxy to release resources

//        }else{
//            Log.d("UDC-->","IMAGE IS NOT PROPER")
//            imageProxy.close()
//
//        }





    }
    private fun isImageProper(imageProxy: ImageProxy): Boolean {

        val mat = Mat()
//        val bmp32: Bitmap = image2.copy(Bitmap.Config.ARGB_8888, true)
//
//        Utils.bitmapToMat(bmp32, mat)
//        val grayImage = Mat()
//        Imgproc.cvtColor(mat, grayImage, Imgproc.COLOR_BGR2GRAY);
//        val laplacian = Mat()
//        Imgproc.Laplacian(grayImage, laplacian, 3)
//        val variance = Core.mean(laplacian).`val`[0]
        return  true
//        if (variance < 100) {
//            return false
//        } else {
//            return true
//        }


    }
    fun Image.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    protected abstract fun detectInImage(image : InputImage) : Task<T>

    abstract fun stop()

    protected abstract fun onSuccess(
        results : List<Face>,
        graphicOverlay: GraphicOverlay<*>,
        rect : Rect
    )

    protected abstract fun onFailure(e : Exception)
    private fun detectHaze(image: ImageProxy): Boolean {
        // Implement your haze detection algorithm here
        // You can analyze image properties like brightness, contrast, etc.
        // to determine whether the image contains haze

        // For simplicity, let's assume a dummy haze detection based on brightness
        val brightnessThreshold = 0.5 // Adjust this threshold as needed
        val brightness = calculateBrightness(image)
        return brightness > brightnessThreshold
    }

    private fun calculateBrightness(image: ImageProxy): Float {
        // Dummy implementation to calculate image brightness
        // You can replace this with a more sophisticated algorithm
        // that analyzes pixel values or histogram data
        // For simplicity, let's assume a grayscale image
        val buffer = image.planes[0].buffer
        var sum = 0f
        while (buffer.hasRemaining()) {
            sum += (buffer.get().toInt() and 0xFF) / 255f
        }
        val pixelCount = image.width * image.height
        return sum / pixelCount
    }
    fun ImageProxy.convertImageProxyToBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}