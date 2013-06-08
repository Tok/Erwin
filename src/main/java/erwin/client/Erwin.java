package erwin.client;

import java.util.HashMap;
import java.util.Map;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import erwin.shared.Complex;
import erwin.shared.Const;
import erwin.shared.enums.Resolution;
import erwin.shared.utils.ColorUtil;
import erwin.shared.utils.WaveUtil;

public class Erwin implements EntryPoint {
    private static final int WIDTH = 400;
    private static final int HEIGHT = WIDTH;
    private static final int FPS_AIM = 250;
    private static final int MS_PER_S = 1000;
    private static final int TIME_DIVISOR = 100;

    private int currentResolution;
    private int bufferWidth;
    private int bufferHeight;

    private final VerticalPanel pan = new VerticalPanel();
    private final Button startButton = new Button("Start");
    private final Button stopButton = new Button("Stop");

    private final Map<Resolution, RadioButton> resolutionBoxes = new HashMap<Resolution, RadioButton>();
    private final Label statusLabel = new Label();
    private final Label fpsLabel = new Label();
    private Canvas can;
    private Canvas buffer;
    private Context2d context;
    private Context2d bufferContext;

    private long zeroFrame = 0L;
    private long lastFrame = 0L;
    private int offset = 0;

    public Erwin() {
        can = Canvas.createIfSupported();
        buffer = Canvas.createIfSupported();
        if (can == null) {
            statusLabel.setText("Your browser doesn't support canvas.");
            return;
        }
        initAllCanvas(Resolution.getDefault());
    }

    public final void onModuleLoad() {
        final Timer timer = new Timer() {
            @Override public void run() {
                final long currentFrame = System.currentTimeMillis();
                final int diff = Long.valueOf(currentFrame - lastFrame).intValue();
                final int fps = diff > 0 ? MS_PER_S / diff : FPS_AIM;
                fpsLabel.setText("FPS: " + fps);
                if (offset < WIDTH) { offset++; } else { offset = 0; }
                drawWaves(currentFrame - zeroFrame);
                context.drawImage(bufferContext.getCanvas(), 0D, 0D, WIDTH, HEIGHT);
                lastFrame = currentFrame;
            }
        };

        startButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                zeroFrame = System.currentTimeMillis();
                timer.scheduleRepeating(MS_PER_S / FPS_AIM);
            }
        });
        stopButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                timer.cancel();
                statusLabel.setText("");
                fpsLabel.setText("");
                reset();
            }
        });

        reset();

        for (final Resolution resolution : Resolution.values()) {
            final RadioButton rb = new RadioButton(Resolution.RESOLUTION_GROUP, resolution.toString());
            rb.setValue(resolution.isDefault());
            rb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override public void onValueChange(final ValueChangeEvent<Boolean> event) {
                    final int res = resolution.getResolution();
                    if (res != 1 || Window.confirm("This may be very slow!")) {
                        initAllCanvas(res);
                    } else {
                        resolutionBoxes.get(Resolution.valueOf(currentResolution)).setValue(true);
                    }
                }
            });
            resolutionBoxes.put(resolution, rb);
        }

        final HorizontalPanel radioPan = new HorizontalPanel();
        radioPan.setSpacing(2);
        radioPan.add(new Label("Resolution"));
        for (final Resolution resolution : Resolution.values()) {
            radioPan.add(resolutionBoxes.get(resolution));
        }
        final HorizontalPanel buttonPan = new HorizontalPanel();
        buttonPan.setSpacing(2);
        buttonPan.add(startButton);
        buttonPan.add(stopButton);

        pan.add(radioPan);
        pan.add(buttonPan);
        pan.add(can);
        pan.add(fpsLabel);
        pan.add(statusLabel);
        RootPanel.get("mainContainer").add(pan);

        startButton.click();
    }

    private void initAllCanvas(final int resolution) {
        currentResolution = resolution;
        bufferWidth = WIDTH / currentResolution;
        bufferHeight = HEIGHT / currentResolution;
        initCanvas(can, false);
        initCanvas(buffer, true);
        context = can.getContext2d();
        bufferContext = buffer.getContext2d();
    }

    private void initCanvas(final Canvas c, final boolean isBuffer) {
        final int width = isBuffer ? bufferWidth : WIDTH;
        final int height = isBuffer ? bufferHeight : HEIGHT;
        c.setWidth(width + "px");
        c.setHeight(height + "px");
        c.setCoordinateSpaceWidth(width);
        c.setCoordinateSpaceHeight(height);
    }

    private void clearCanvas() {
        bufferContext.beginPath();
        bufferContext.setFillStyle(Const.BLACK);
        bufferContext.fillRect(0D, 0D, bufferWidth, bufferHeight);
        bufferContext.closePath();
        context.drawImage(bufferContext.getCanvas(), 0D, 0D, WIDTH, HEIGHT);
    }

    private void reset() {
        final String wlDefault = String.valueOf(Const.DEFAULT_WAVELENGTH);
        RootPanel.get("wavelength").getElement().setInnerHTML(wlDefault);
        final String sliderDefault = String.valueOf(Const.DEFAULT_WAVELENGTH * 100);
        RootPanel.get("wlSlider").getElement().setPropertyString("value", sliderDefault);
        clearCanvas();
        offset = 0;
    }

    private void drawWaves(final long t) {
        final double wavelength = Double.valueOf(RootPanel.get("wavelength").getElement().getInnerHTML());
        final int x0 = bufferWidth / 2;
        final int y0 = bufferHeight / 2;
        final long tt = Double.valueOf(Double.valueOf(t) / TIME_DIVISOR).longValue();
        for (int x = 0; x < bufferWidth; x++) {
            for (int y = 0; y < bufferHeight; y++) {
                final Complex c = WaveUtil.calculateWave(x - x0, y - y0, tt, wavelength);
                bufferContext.setFillStyle(ColorUtil.getColor(c));
                bufferContext.fillRect(x, y, 1D, 1D);
            }
        }
        bufferContext.setFillStyle(Const.WHITE);
        bufferContext.fillRect(x0, y0, 1D, 1D);
    }
}
