package erwin.client;

import java.util.HashMap;
import java.util.Map;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import erwin.shared.Complex;
import erwin.shared.Const;
import erwin.shared.enums.AnimationMode;
import erwin.shared.enums.Operator;
import erwin.shared.enums.Resolution;
import erwin.shared.utils.ColorUtil;
import erwin.shared.utils.WaveCalc;

public class Erwin implements EntryPoint {
    private static final int WIDTH = 400;
    private static final int HEIGHT = WIDTH;
    private static final int FPS_AIM = 250;
    private static final int MS_PER_S = 1000;
    private static final int TIME_DIVISOR = 100;
    private static final String LABEL_STYLE = "lab";

    private final VerticalPanel pan = new VerticalPanel();
    private final Label resolutionLabel = new Label("Resolution");
    private final Map<Resolution, RadioButton> resolutionBoxes = new HashMap<Resolution, RadioButton>();
    private final Label operatorLabel = new Label("Operator");
    private final RadioButton addRb = new RadioButton(Operator.GROUP_NAME, Operator.ADD.toString());
    private final RadioButton mulRb = new RadioButton(Operator.GROUP_NAME, Operator.MULTIPLY.toString());
    private final Label magnitudeLabel = new Label("Magnitude");
    private final CheckBox useMagnitude = new CheckBox("Calculate");
    private final Button startButton = new Button("Start");
    private final Button stopButton = new Button("Stop");
    private final Button resetButton = new Button("Reset");
    private final ListBox animationBox = new ListBox();
    private Canvas can;
    private Canvas buffer;
    private Context2d context;
    private Context2d bufferContext;
    private final Label fpsLabel = new Label();
    private final Label statusLabel = new Label();
    private Timer timer;

    private long zeroFrame = 0L;
    private long lastFrame = 0L;

    private WaveCalc waveCalc;
    private Complex[] pixels;
    private int currentResolution;
    private int bufferWidth;
    private int bufferHeight;
    private int x0;
    private int y0;

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
        timer = new Timer() {
            @Override public void run() {
                final long currentFrame = System.currentTimeMillis();
                final int diff = Long.valueOf(currentFrame - lastFrame).intValue();
                final int fps = diff > 0 ? MS_PER_S / diff : FPS_AIM;
                fpsLabel.setText("FPS: " + fps);
                final long t = Double.valueOf(Double.valueOf(currentFrame - zeroFrame) / TIME_DIVISOR).longValue();
                final double wavenumber = Double.valueOf(RootPanel.get("waveNumber").getElement().getInnerHTML());
                final AnimationMode aniMode = AnimationMode.valueOf(animationBox.getItemText(animationBox.getSelectedIndex()).toUpperCase());
                if (aniMode.equals(AnimationMode.CENTER)) {
                    drawWaves(t, wavenumber);
                } else if (aniMode.equals(AnimationMode.DUAL)) {
                    drawDual(t, wavenumber);
                } else if (aniMode.equals(AnimationMode.MANDALA)) {
                    drawMandala(t, wavenumber);
                }
                context.drawImage(bufferContext.getCanvas(), 0D, 0D, WIDTH, HEIGHT);
                lastFrame = currentFrame;
            }
        };

        for (final Resolution resolution : Resolution.values()) {
            final RadioButton rb = new RadioButton(Resolution.GROUP_NAME, resolution.toString());
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

        useMagnitude.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(final ValueChangeEvent<Boolean> event) {
                waveCalc = new WaveCalc(x0, y0, event.getValue());
            }
        });

        startButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                start();
            }
        });
        stopButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                stop();
            }
        });
        resetButton.addClickHandler(new ClickHandler() {
            @Override public void onClick(final ClickEvent event) {
                animationBox.setSelectedIndex(0);
                reset();
                start();
            }
        });

        for (final AnimationMode mode : AnimationMode.values()) {
            animationBox.addItem(mode.toString());
        }

        animationBox.addChangeHandler(new ChangeHandler() {
            @Override public void onChange(final ChangeEvent event) {
                reset();
            }
        });

        reset();

        final HorizontalPanel radioPan = new HorizontalPanel();
        resolutionLabel.setStyleName(LABEL_STYLE);
        radioPan.add(resolutionLabel);
        for (final Resolution resolution : Resolution.values()) {
            radioPan.add(resolutionBoxes.get(resolution));
        }
        final HorizontalPanel operatorPan = new HorizontalPanel();
        operatorLabel.setStyleName(LABEL_STYLE);
        operatorPan.add(operatorLabel);
        operatorPan.add(addRb);
        operatorPan.add(mulRb);
        final HorizontalPanel magnitudePan = new HorizontalPanel();
        magnitudeLabel.setStyleName(LABEL_STYLE);
        magnitudePan.add(magnitudeLabel);
        magnitudePan.add(useMagnitude);
        final HorizontalPanel buttonPan = new HorizontalPanel();
        buttonPan.add(startButton);
        buttonPan.add(stopButton);
        buttonPan.add(resetButton);
        buttonPan.add(animationBox);

        pan.add(radioPan);
        pan.add(operatorPan);
        pan.add(magnitudePan);
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
        pixels = new Complex[bufferWidth * bufferHeight];
        initPixels();
        x0 = bufferWidth / 2;
        y0 = bufferHeight / 2;
        waveCalc = new WaveCalc(x0, y0, useMagnitude.getValue());
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
        final String wlDefault = String.valueOf(Const.DEFAULT_WAVENUMBER);
        RootPanel.get("waveNumber").getElement().setInnerHTML(wlDefault);
        final String sliderDefault = String.valueOf(Const.DEFAULT_WAVENUMBER * 100);
        RootPanel.get("waveSlider").getElement().setPropertyString("value", sliderDefault);
        initAllCanvas(Resolution.getDefault());
        resolutionBoxes.get(Resolution.valueOf(currentResolution)).setValue(true);
        addRb.setValue(true);
        statusLabel.setText("");
        fpsLabel.setText("");
        clearCanvas();
    }

    private void start() {
        zeroFrame = System.currentTimeMillis();
        timer.scheduleRepeating(MS_PER_S / FPS_AIM);
    }

    private void stop() {
        timer.cancel();
    }

    private void initPixels() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Complex.valueOf(0D, 0D);
        }
    }

    private void drawWaves(final long t, final double waveNumber) {
        for (int x = 0; x < bufferWidth; x++) {
            for (int y = 0; y < bufferHeight; y++) {
                final Operator op = mulRb.getValue() ? Operator.MULTIPLY : Operator.ADD;
                final Complex c = waveCalc.calculateWave(x, y, t, waveNumber, op);
                bufferContext.setFillStyle(ColorUtil.getColor(c));
                bufferContext.fillRect(x, y, 1D, 1D);
            }
        }
        bufferContext.setFillStyle(Const.WHITE);
        bufferContext.fillRect(x0, y0, 1D, 1D); //center
    }

    private void drawDual(final long t, final double waveNumber) {
        final int centerX1 = bufferWidth / 3;
        final int centerX2 = bufferWidth / 3 * 2;
        final int centerY1 = bufferHeight / 2;
        final int centerY2 = bufferHeight / 2;
        final Operator op = mulRb.getValue() ? Operator.MULTIPLY : Operator.ADD;
        for (int x = 0; x < bufferWidth; x++) {
            for (int y = 0; y < bufferHeight; y++) {
                final Complex r1 = waveCalc.calculateDual(x, y, centerX1, centerY1, t, waveNumber);
                final Complex r2 = waveCalc.calculateDual(x, y, centerX2, centerY2, t, waveNumber);
                final Complex both = op.equals(Operator.MULTIPLY) ? r1.multiply(r2) : r1.add(r2);
                bufferContext.setFillStyle(ColorUtil.getColor(both));
                bufferContext.fillRect(x, y, 1D, 1D);
            }
        }
        bufferContext.setFillStyle(Const.WHITE);
        bufferContext.fillRect(centerX1, centerY1, 1D, 1D);
        bufferContext.fillRect(centerX2, centerY2, 1D, 1D);
    }

    private void drawMandala(final long t, final double waveNumber) {
        final Operator op = mulRb.getValue() ? Operator.MULTIPLY : Operator.ADD;
        for (int x = 0; x < bufferWidth; x++) {
            for (int y = 0; y < bufferHeight; y++) {
                final int index = (x * bufferWidth) + y;
                final Complex old = pixels[index];
                final Complex n = waveCalc.calculateMandala(x, y, t, waveNumber, op, old);
                bufferContext.setFillStyle(ColorUtil.getColor(n));
                bufferContext.fillRect(x, y, 1D, 1D);
                pixels[index] = n;
            }
        }
        bufferContext.setFillStyle(Const.WHITE);
        bufferContext.fillRect(x0, y0, 1D, 1D);
    }
}
