package by.vlas.bt_app1.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import by.vlas.bt_app1.R;
import by.vlas.bt_app1.RequestCodes;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Bluetooth_App1";
    private boolean isBluetoothWasEnabled;
    private TextView console;
    private ScrollView consoleScroll;
    private BluetoothAdapter bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isBluetoothWasEnabled) {
            if (isBluetoothReady()) {
                bt.disable();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setup() {
        consoleScroll = (ScrollView) findViewById(R.id.console_scroll);
        console = (TextView) findViewById(R.id.console);
        setupBluetooth();
        setupButtons();
    }

    private void setupBluetooth() {
        bt = BluetoothAdapter.getDefaultAdapter();
        if (bt == null) {
            toastBluetoothNotAvailable();
            return;
        }
        isBluetoothWasEnabled = bt.isEnabled();
        if (!isBluetoothWasEnabled) {
            tryEnableBluetooth();
        }
    }

    private void tryEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            startActivityForResult(enableBtIntent, RequestCodes.REQUEST_ENABLE_BT);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error enabling bluetooth.", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RequestCodes.REQUEST_ENABLE_BT == requestCode) {
            String text = "Bluetooth is " + (RESULT_OK == resultCode ? "" : "NOT ") + "enabled";
            Toast.makeText(this, text, Toast.LENGTH_LONG)
                    .show();
            printBluetoothInfo();
        }
    }

    private void printBluetoothInfo() {
        if (isBluetoothReady()) {
            String btAddr = bt.getAddress();
            String deviceName = bt.getName();
            int btState = bt.getState();
            String status = "{btAddr: '" + btAddr + "', deviceName: '" + deviceName + "', BT_state: '" + btState + "'}";
            addLine(console, status);
        }
    }

    private boolean isBluetoothReady() {
        if (bt == null) {
            toastBluetoothNotAvailable();
        } else if (!bt.isEnabled()) {
            Toast.makeText(this, "Bluetooth is disabled.", Toast.LENGTH_LONG)
                    .show();
        }
        return bt != null && bt.isEnabled();
    }

    private void toastBluetoothNotAvailable() {
        Toast.makeText(this, "Bluetooth is not available in this device.", Toast.LENGTH_LONG)
                .show();
    }

    private void setupButtons() {
        final Button btnConnect = (Button) findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(this::btnConnectAction);
        final Button btnOn = (Button) findViewById(R.id.btn_on);
        btnOn.setOnClickListener(this::btnOnAction);
        final Button btnOff = (Button) findViewById(R.id.btn_off);
        btnOff.setOnClickListener(this::btnOffAction);
    }

    public void btnConnectAction(View view) {
        addLine(console, "{btn: 'Connect'}");
        if (isBluetoothReady()) {
            BluetoothDevice device = bt.getRemoteDevice("00:13:02:01:00:09");
            BluetoothClass bluetoothClass = device.getBluetoothClass();
        }
    }

    public void btnOnAction(View view) {
        addLine(console, "{btn: 'On'}");
    }

    public void btnOffAction(View view) {
        addLine(console, "{btn: 'Off'}");

    }

    private void addLine(TextView textView, String textLine) {
        textView.setText(textView.getText() + "\n" + textLine);
        consoleScroll.fullScroll(View.FOCUS_DOWN);
    }
}
