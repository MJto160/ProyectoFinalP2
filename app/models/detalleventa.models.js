import { DataTypes } from 'sequelize';

export default (sequelize) => {
  return sequelize.define('DetalleVenta', {
    id_detalle: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true
    },
    id_venta: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    id_localidad: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    id_partido: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    cantidad: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    precio_unitario: {
      type: DataTypes.DECIMAL(10, 2),
      allowNull: false
    }
  }, {
    tableName: 'detalle_ventas',
    timestamps: false
  });
};