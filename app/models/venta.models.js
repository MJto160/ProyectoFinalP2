import { DataTypes } from 'sequelize';

export default (sequelize) => {
  return sequelize.define('ventas', {
    id_venta: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true
    },
    id_vendedor: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    fecha_venta: {
      type: DataTypes.DATE,
      defaultValue: DataTypes.NOW
    },
    total_venta: {
      type: DataTypes.DECIMAL(10, 2),
      allowNull: false
    }
  }, {
    tableName: 'ventas',
    timestamps: false
  });
};