package org.example.simulacionpedidos.orders;

public class order {
    private final Long id;
    private final Double total;
    private final String customerName;

    // Constructor para inicializar el pedido
    public order(Long id, Double total, String customerName) {
        this.id = id;
        this.total = total;
        this.customerName = customerName;
    }

    // Getters necesarios
    public Long getId() {
        return id;
    }

    public Double getTotal() {
        return total;
    }

    public String getCustomerName() {
        return customerName;
    }

    // toString personalizado para la salida del log
    @Override
    public String toString() {
        return "Pedido " + id + " para el cliente: " + customerName;
    }
}