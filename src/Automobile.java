public class Automobile {
    private final String registration;
    private double fuelLiters;
    private double mileageKm;
    private double speedKmh;
    private int gear;

    public Automobile(String registration) {
        if (registration == null || registration.isBlank()) {
            throw new IllegalArgumentException("Нужен регистрационный номер");
        }
        this.registration = registration;
        this.fuelLiters = 50;
        this.speedKmh = 60;
        this.gear = 1;
    }

    public void startEngine(String ignitionKey, int attempts) {
        System.out.printf("  запуск ключом «%s», попыток: %d%n", ignitionKey, attempts);
    }

    @Repeat(7)
    public void drive(double distanceKm, int speedKmh) {
        this.speedKmh = speedKmh;
        this.mileageKm += distanceKm;
        System.out.printf("  поездка %.1f км на %d км/ч%n", distanceKm, speedKmh);
    }

    public void refuel(Fuel fuel, double liters) {
        this.fuelLiters += liters;
        System.out.printf("  заправка %s на %.1f л, бак %.1f л%n", fuel, liters, fuelLiters);
    }

    @Repeat(2)
    protected void shiftGear(int gear, double rpm) {
        this.gear = Math.clamp(gear, 1, 5);
        System.out.printf("  передача %d, обороты %.1f%n", this.gear, rpm);
    }

    @Repeat(3)
    protected void brake(double deceleration, boolean absEnabled) {
        speedKmh = Math.max(0, speedKmh - deceleration);
        System.out.printf(
                "  торможение %.1f, ABS %s, скорость %.1f км/ч%n",
                deceleration,
                absEnabled ? "вкл" : "выкл",
                speedKmh);
    }

    protected void warmUp(int seconds, double idleRpm) {
        System.out.printf("  прогрев %d с при %.1f об/мин%n", seconds, idleRpm);
    }

    @Repeat(4)
    private void consumeFuel(double liters, double kmPerLiter) {
        fuelLiters = Math.max(0, fuelLiters - liters);
        mileageKm += liters * kmPerLiter;
        System.out.printf(
                "  расход %.1f л, пробег %.1f км, бак %.1f л%n",
                liters,
                mileageKm,
                fuelLiters);
    }

    @Repeat(1)
    private void registerService(String station, long odometer, Fuel fuel) {
        System.out.printf("  ТО: %s, одометр %d, топливо %s%n", station, odometer, fuel);
    }

    private void checkTirePressure(double pressureBar, int wheelIndex) {
        System.out.printf("  давление %.1f бар, колесо %d%n", pressureBar, wheelIndex);
    }

    @Override
    public String toString() {
        return String.format(
                "Automobile[%s]: бак %.1f л, пробег %.1f км, скорость %.1f км/ч, передача %d",
                registration,
                fuelLiters,
                mileageKm,
                speedKmh,
                gear);
    }
}
