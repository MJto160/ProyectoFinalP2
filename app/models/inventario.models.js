import { DataTypes } from 'sequelize';

export default (sequelize) => {
  return sequelize.define('Inventario', {
    id_inventario: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true
    },
    id_partido: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    id_localidad: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    cantidad_total: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    precio: {
      type: DataTypes.DECIMAL(10, 2),
      allowNull: false,
      defaultValue: 0.00 
    },
    cantidad_disponible: {
      type: DataTypes.INTEGER,
      allowNull: false
    }
  }, {
    tableName: 'inventario_boletos',
    timestamps: false
  });
};