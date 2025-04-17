package com.example.databaseornek;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editTextName, editTextSurname, editTextEmail;
    Button buttonImage, buttonSave, buttonUpdate, buttonDelete;
    ImageView imageView;
    ListView listViewUsers;
    ArrayAdapter<String> adapter;
    DBHelper dbHelper;

    private static final int IMAGE_PICK_CODE = 1000;
    private byte[] selectedImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonImage = findViewById(R.id.buttonImage);
        buttonSave = findViewById(R.id.buttonSave);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDelete = findViewById(R.id.buttonDelete);
        imageView = findViewById(R.id.imageView);
        listViewUsers = findViewById(R.id.listViewUsers);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewUsers.setAdapter(adapter);

        loadUsers();

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter.getItem(position);
                if (item != null) {
                    String[] userDetails = item.split(":");
                    editTextName.setText(userDetails[0]);
                    editTextSurname.setText(userDetails[1]);
                    editTextEmail.setText(userDetails[2]);
                    byte[] imageBytes = dbHelper.getUserImage(userDetails[0], userDetails[1], userDetails[2]);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            }
        });

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageView.setImageURI(data.getData());
            Bitmap bitmap = BitmapFactory.decodeFile(getPath(data.getData()));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            selectedImage = stream.toByteArray();
        }
    }

    private String getPath(android.net.Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String s = cursor.getString(column_index);
            cursor.close();
            return s;
        }
        return null;
    }

    private void saveUser() {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        if (!name.isEmpty() && !surname.isEmpty() && !email.isEmpty() && selectedImage != null) {
            long result = dbHelper.insertUser(name, surname, email, selectedImage);
            if (result > 0) {
                Toast.makeText(this, "KAYIT BAŞARILI OLDU", Toast.LENGTH_SHORT).show();
                loadUsers();
                clearFields();
            } else {
                Toast.makeText(this, "Kullanıcı kaydedilirken bir hata oluştu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Tüm alanları doldurun ve bir resim seçin", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser() {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        if (!name.isEmpty() && !surname.isEmpty() && !email.isEmpty()) {
            long result = dbHelper.updateUser(name, surname, email, selectedImage);
            if (result > 0) {
                Toast.makeText(this, "GÜNCELLENDİ", Toast.LENGTH_SHORT).show();
                loadUsers();
                clearFields();
            } else {
                Toast.makeText(this, "Kullanıcı güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Kullanıcı seçilmedi veya tüm alanları doldurun", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteUser() {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        if (!name.isEmpty() && !surname.isEmpty() && !email.isEmpty()) {
            long result = dbHelper.deleteUser(name, surname, email);
            if (result > 0) {
                Toast.makeText(this, "KULLANICI SİLİNDİ", Toast.LENGTH_SHORT).show();
                loadUsers();
                clearFields();
            } else {
                Toast.makeText(this, "Kullanıcı silinirken bir hata oluştu", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Kullanıcı seçilmedi veya tüm alanları doldurun", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUsers() {
        ArrayList<String> usersList = dbHelper.getAllUsers();
        adapter.clear();
        adapter.addAll(usersList);
        adapter.notifyDataSetChanged();
    }

    private void clearFields() {
        editTextName.setText("");
        editTextSurname.setText("");
        editTextEmail.setText("");
        imageView.setImageDrawable(null);
        selectedImage = null;
    }
}
