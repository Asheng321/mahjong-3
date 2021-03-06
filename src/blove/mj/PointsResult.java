package blove.mj;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import blove.mj.GameResult.WinInfo;
import blove.mj.board.PlayerTiles;
import blove.mj.rules.WinStrategy;

/**
 * 一局游戏结果分数。
 * 
 * @author blovemaple
 */
public class PointsResult implements Serializable {
	private static final long serialVersionUID = -2700742284298994146L;

	private final int basicPoints;
	private final Set<PointItem> pointItems;
	private final WinInfo winInfo;
	private final PlayerLocation dealerLocation;
	private final int dealerMultiple;

	public static PointsResult generate(Map<PlayerLocation, PlayerTiles> tiles,
			PlayerLocation dealerLocation, WinInfo winInfo,
			WinStrategy winStrategy) {
		int basicPoints = winStrategy.getBasicPoints();
		int dealerMultiple = winStrategy.getDealerMultiple();
		Set<PointItem> pointItems = winInfo != null ? winStrategy
				.getPoints(tiles.get(winInfo.getWinnerLocation()))
				: Collections.<PointItem> emptySet();
		return new PointsResult(basicPoints, pointItems, winInfo,
				dealerLocation, dealerMultiple);
	}

	private PointsResult(int basicPoints, Set<PointItem> pointItems,
			WinInfo winInfo, PlayerLocation dealerLocation, int dealerMultiple) {
		this.basicPoints = basicPoints;
		this.pointItems = pointItems;
		this.winInfo = winInfo;
		this.dealerLocation = dealerLocation;
		this.dealerMultiple = dealerMultiple;
	}

	/**
	 * 返回和牌者的基本得分。
	 * 
	 * @return 基本得分
	 */
	public int getBasicPoints() {
		return basicPoints;
	}

	/**
	 * 返回所有得分项目。
	 * 
	 * @return 所有得分项目
	 */
	public Set<PointItem> getPointItems() {
		return pointItems;
	}

	/**
	 * 返回指定位置玩家的得分。
	 * 
	 * @param location
	 *            位置
	 * @return 得分
	 */
	public int getPoints(PlayerLocation location) {
		if (winInfo == null)
			return 0;

		boolean isSelfDrawWin = winInfo.getPaoerLocation() == null;
		boolean isWinner = location.equals(winInfo.getWinnerLocation());
		boolean isPaoer = location.equals(winInfo.getPaoerLocation());

		if (!isSelfDrawWin && !isWinner && !isPaoer) {
			return -basicPoints;
		}

		boolean winnerIsDealer = winInfo.getWinnerLocation().equals(
				dealerLocation);
		int itemPoints = 0;
		for (PointItem item : pointItems)
			itemPoints += item.getPoints();

		int points;
		if (isWinner) {
			if (isSelfDrawWin)
				points = (basicPoints + itemPoints) * 3;
			else
				points = basicPoints * 3 + itemPoints;
			if (winnerIsDealer)
				points *= dealerMultiple;
		} else {
			points = -basicPoints;
			if (isSelfDrawWin || isPaoer)
				points -= itemPoints;
			if (winnerIsDealer)
				points *= dealerMultiple;
		}

		return points;
	}
}
