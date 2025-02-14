package chemotaxis.g3;

import java.util.ArrayList;
import java.util.Map;

import chemotaxis.sim.DirectionType;
import chemotaxis.sim.ChemicalCell;
import chemotaxis.sim.ChemicalCell.ChemicalType;
import chemotaxis.sim.Move;
import chemotaxis.sim.SimPrinter;

public class Agent extends chemotaxis.sim.Agent {

    /**
     * Agent constructor
     *
     * @param simPrinter  simulation printer
     *
     */
	public Agent(SimPrinter simPrinter) {
		super(simPrinter);
	}

    /**
     * Move agent
     *
     * @param randomNum        random number available for agents
     * @param previousState    byte of previous state
     * @param currentCell      current cell
     * @param neighborMap      map of cell's neighbors
     * @return                 agent move
     *
     */
	@Override
	public Move makeMove(Integer randomNum, Byte previousState, ChemicalCell currentCell, Map<DirectionType, ChemicalCell> neighborMap) {
		Move move = new Move();

		// see highest in hierarchy color is sees in its space or one immediately adjacent:
		// (highest) blue, green, red (lowest)
		// set that chemical to chosen chemical type
		ChemicalType highest_priority = ChemicalType.RED;
		if(currentCell.getConcentration(ChemicalType.BLUE) != 0)
		{
			highest_priority = ChemicalType.BLUE;
		}
		else if(currentCell.getConcentration(ChemicalType.GREEN) != 0)
		{
			highest_priority = ChemicalType.GREEN;
		}
		for(DirectionType directionType : neighborMap.keySet())
		{
			if(neighborMap.get(directionType).getConcentration(ChemicalType.BLUE) != 0)
			{
				highest_priority = ChemicalType.BLUE;
			}
			else if(highest_priority != ChemicalType.BLUE &&
					(neighborMap.get(directionType).getConcentration(ChemicalType.GREEN) != 0))
			{
				highest_priority = ChemicalType.GREEN;
			}
		}

		ChemicalType chosenChemicalType = highest_priority;

		// initialize the previousState byte using the random number provided
		if(previousState==0)
		{
			move.currentState = (byte)(Math.abs(randomNum%4) + 1);
		}
		else
		{
			move.currentState = previousState;
		}

		// set the direction of the agent if chemicals are detected
		double highestConcentration = currentCell.getConcentration(chosenChemicalType);
		for (DirectionType directionType : neighborMap.keySet()) {
			if (neighborMap.get(directionType).getConcentration(chosenChemicalType) == 0) {
			}
			if (highestConcentration <= neighborMap.get(directionType).getConcentration(chosenChemicalType)) {
				highestConcentration = neighborMap.get(directionType).getConcentration(chosenChemicalType);
				move.directionType = directionType;
			}
		}
		// set a new state for that agent (every randomNum%3 == 2 turn), to avoid repeated collisions
		if(randomNum%3 == 2)
		{
			move.currentState = (byte)(Math.abs(randomNum%4) + 1);
		}
		// agent movement in absence of chemicals
		/* all surrounding cells have no chemical
		Direction based on randNum%4
			- 0: right
			- 1: down
			- 2: left
			- 3: up
		*/
		if (highestConcentration == 0) {
			DirectionType priority;
			if (move.currentState == 1) {
				priority = DirectionType.SOUTH;
			} else if (move.currentState == 2) {
				priority = DirectionType.NORTH;
			} else if (move.currentState == 3) {
				priority = DirectionType.EAST;
			} else{
				priority = DirectionType.WEST;

			}
			// if the point where agent wants to randomly move to is open, go to that point
			if(neighborMap.get(priority).isOpen())
			{
				move.directionType = priority;
			}
			else
			{// if the point is blocked, search for viable directions to go to and choose one randomly
				ArrayList<DirectionType> options = new ArrayList<DirectionType>();
				for (DirectionType directionType : neighborMap.keySet()) {
					if(neighborMap.get(directionType).isOpen())
					{
						options.add(directionType);
					}
				}
				if(options.size() != 0)
				{
					move.directionType = options.get(randomNum%options.size());
				}
				// set a new state for that agent
				move.currentState = (byte)(Math.abs(randomNum%4) + 1);
			}
		}
		return move;
	}
}