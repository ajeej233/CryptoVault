package com.abotlogix.sample;

import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.abotlogix.cryptovault.CryptoVault;
import com.abotlogix.sample.databinding.ActivityMainBinding;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class JavaSampleActivity extends AppCompatActivity {
    @Inject
    CryptoVault cryptoVault;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSave.setOnClickListener(v -> {
            String key = binding.etKey.getText().toString();
            String value = binding.etValue.getText().toString();

            if (!key.isEmpty() && !value.isEmpty()) {

                    cryptoVault.putString(key, value);
                    Toast.makeText(this, "Saved securely!", Toast.LENGTH_SHORT).show();

            }


        });

        binding.btnRetrieve.setOnClickListener(v -> {
            String key = binding.etKey.getText().toString();
            if (!key.isEmpty()) {
                    String value = cryptoVault.getStringStream(key).toString();
                CompletableFuture<String> future =  cryptoVault.getStringFuture(key);
                future.thenAccept(userId -> {
                    binding.tvResult.setText( "Retrieved: "+userId);

                    // Use decrypted value
                });

            }
        });
        binding.btndelete.setOnClickListener(v -> {
            String key = binding.etKey.getText().toString();
            if (!key.isEmpty()) {
                cryptoVault.delete(key);
                Toast.makeText(this, "Deleted securely!", Toast.LENGTH_SHORT).show();

            }
        });






    }


}