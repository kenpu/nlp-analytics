var React = require('react');
var sprintf = require('sprintf');

function makeRow(o) {
	var offsets = [];
	for(var i=0; i < o.offset || 0; i++) {
		var mykey = o.key + "." + (i+1);
		offsets.push(<td key={mykey}></td>)
	}
	var padding = [];
	for(var i=o.offset; i < o.totalLength - o.colSpan; i ++)
		padding.push(<td />);

	return (
		<tr>{offsets}<td colSpan={o.colSpan}>{o.content}</td>{padding}</tr>
	);
}

var Reply = React.createClass({
	render: function() {
		var reply = this.props.reply;

		var totalLength = reply.words.length;
		var Words = reply.words.map(function(w, i) {
			return <td key={i}><span className="word">{w}</span></td>;
		});

		var scoresView = [];
		["blm_1", "blm_2", "blm_3", "blm_4", "blm_5"].forEach(function(blmName, idx) {
			var blm = reply[blmName];
			var deviation = blm.deviations;
			var spanSize = blm.spanSize;
			scoresView.push(<tr>{Words}</tr>);
			blm.spanScores.forEach(function(score, wordIndex) {
				if(typeof(score) != "number")
					score = -1000
				scoresView.push(makeRow({
					content: (<span>{sprintf("%.3f (%.3f)",score,deviation[wordIndex])}</span>),
					offset: wordIndex,
					colSpan: spanSize,
					key: {wordIndex},
					totalLength: totalLength,
				}));
			});
		});

		return (
			<table className="table table-bordered">
				<tbody>
					{scoresView}
				</tbody>
			</table>
		);
	}
});

var Replies = React.createClass({
	render: function() {
		var replies = this.props.replies;
		var replyViews = replies.map(function(reply, i) {
			return (
				<Reply reply={reply} key={i} />
			);
		});

		return (
			<div className="replies">
				{replyViews}
			</div>
		);
	}
});

module.exports = Replies;
