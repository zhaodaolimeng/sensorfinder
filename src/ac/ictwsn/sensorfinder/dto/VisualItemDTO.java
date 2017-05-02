package ac.ictwsn.sensorfinder.dto;


/**
 * Used for visualization, structured as the follows:
 * 
 * _____|_spatial_|_temporal_|_wordfreq_|_semantic_|
 * item0|____1____|____3_____|_____0____|______1___|
 * item1|____0____|____1_____|_____3____|______1___|
 * item2|......
 * ...
 * 
 * @author li
 *
 */
public class VisualItemDTO {
	
	Double spatial;
	Double temporal;
	Double wordfreq;
	Double semantic;
	
	public Double getSpatial() {
		return spatial;
	}
	public void setSpatial(Double spatial) {
		this.spatial = spatial;
	}
	public Double getTemporal() {
		return temporal;
	}
	public void setTemporal(Double temporal) {
		this.temporal = temporal;
	}
	public Double getWordfreq() {
		return wordfreq;
	}
	public void setWordfreq(Double wordfreq) {
		this.wordfreq = wordfreq;
	}
	public Double getSemantic() {
		return semantic;
	}
	public void setSemantic(Double semantic) {
		this.semantic = semantic;
	}
	
}
